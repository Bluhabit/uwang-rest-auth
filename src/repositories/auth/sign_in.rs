use std::collections::HashMap;

use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, QueryFilter};
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::jwt::encode;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::{check_account_status_active_user, create_session_from_user, create_session_redis_from_user};
use crate::entity::{user_credential, user_verification};
use crate::entity::sea_orm_active_enums::AuthProvider;
use crate::entity::user_credential::Model as UserCredential;
use crate::models::auth::{OtpRedisModel, SessionRedisModel};
use crate::models::utils::create_user_verification;

#[derive(Debug, Clone)]
pub struct SignInRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignInRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    pub async fn get_user_credential(
        &self,
        user_id: String,
    ) -> Result<UserCredential, ErrorResponse> {
        let user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;
        if user.is_err() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        let credential = user.unwrap();
        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        Ok(credential.unwrap())
    }

    pub async fn get_user_by_email_sign_in(
        &self,
        request_password: &str,
        email: &str,
        auth_provider: AuthProvider,
    ) -> Result<UserCredential, ErrorResponse> {
        let user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db).await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan".to_string()));
        }
        let credential_result = user.unwrap();

        if credential_result.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan".to_string()));
        }

        if auth_provider == AuthProvider::Basic {
            let user = credential_result.clone().unwrap();
            if bcrypt::verify(&request_password, &user.password).is_err() {
                return Err(ErrorResponse::bad_request(
                    1007,
                    "Email atau password salah atau tidak sesuai".to_string(),
                ));
            }
        }

        let check_status = check_account_status_active_user(
            &credential_result.clone().unwrap()
        );
        if check_status.is_err() {
            return check_status;
        }

        if check_status.is_err() {
            return Err(
                ErrorResponse::bad_request(
                    1002,
                    "Akun belum terdaftar".to_string(),
                )
            );
        }

        let user_credential = check_status.unwrap();

        if user_credential.auth_provider != auth_provider {
            return Err(
                ErrorResponse::bad_request(
                    1003,
                    "Akun sudah digunakan".to_string(),
                )
            );
        }
        return Ok(credential_result.unwrap());
    }


    pub async fn create_user_verification(
        &self,
        user: user_credential::Model,
    ) -> Result<user_verification::Model, ErrorResponse> {
        let verification = create_user_verification(user);

        let saved = verification.insert(&self.db).await;

        if saved.is_err() {
            println!("Error create verification {}", saved.err().unwrap().to_string());
            return Err(ErrorResponse::unauthorized("".to_string()));
        }

        Ok(saved.unwrap())
    }

    pub async fn save_otp_sign_in_to_redis(
        &mut self,
        verification_id: &str,
        otp: &str,
        user_id: &str,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        let session_id = RedisUtil::new(verification_id)
            .create_key_session_sign_in();

        let saved: Result<String, redis::RedisError> = connection
            .unwrap()
            .hset_multiple(session_id.clone(), &[
                (common::constant::REDIS_KEY_OTP, otp),
                (common::constant::REDIS_KEY_USER_ID, user_id),
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(session_id.clone(), 60);

        if saved.is_err() {
            return Err(ErrorResponse::unauthorized(
                saved.unwrap_err().to_string())
            );
        }
        let data = OtpRedisModel {
            otp: otp.to_string(),
            user_id: user_id.to_string(),
            session_id:session_id
        };
        Ok(data)
    }
    pub async fn get_otp_sign_in_from_redis(
        &self,
        session_id: &str,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        let redis = self.cache
            .get_connection();
        if redis.is_err() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        let session_key = RedisUtil::new(session_id)
            .create_key_otp_sign_in();
        let session_redis: RedisResult<HashMap<String, String>> = redis
            .unwrap()
            .hgetall(session_key.clone());
        if session_redis.is_err() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        let redis = session_redis.unwrap();
        let otp = redis.get(common::constant::REDIS_KEY_OTP);
        let user_id = redis.get(common::constant::REDIS_KEY_USER_ID);
        if otp.is_none() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        let data = OtpRedisModel {
            user_id: user_id.unwrap().to_string(),
            otp: otp.unwrap().to_string(),
            session_id:session_key
        };

        Ok(data)
    }
    pub async fn save_user_session_to_redis(
        &self,
        user: &UserCredential,
    ) -> Result<SessionRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        if connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let session_id = Uuid::new_v4();
        let session_key = RedisUtil::new(session_id.to_string().as_str())
            .create_key_otp_sign_in();


        let generate_token = encode(session_id.to_string());
        if generate_token.is_none() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let _: Result<String, redis::RedisError> = connection
            .unwrap()
            .hset_multiple(
                session_key,
                &*create_session_redis_from_user(
                    user.clone(),
                    generate_token
                        .clone()
                        .unwrap(),
                ),
            );

        Ok(create_session_from_user(
            user.to_owned(),
            generate_token.unwrap(),
        ))
    }
}
