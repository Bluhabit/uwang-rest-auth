use std::collections::HashMap;

use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, QueryFilter};
use sea_orm::ActiveValue::Set;
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::jwt::encode;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::{check_account_status_active_user, create_session_from_user, create_session_redis_from_user};
use crate::entity::{user_credential, user_profile, user_verification};
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::{Model as UserCredential, Model};
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

    // sign in email & password

    pub async fn get_user_by_email(
        &self,
        request_password: &str,
        email: &str,
    ) -> Result<UserCredential, ErrorResponse> {
        let user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db).await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [3]".to_string()));
        }
        let credential_result = user.unwrap();

        if credential_result.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [4]".to_string()));
        }
        let user = credential_result.clone().unwrap();
        if bcrypt::verify(&request_password, &user.password).is_err() {
            return Err(ErrorResponse::bad_request(
                1007,
                "Email atau password salah atau tidak sesuai".to_string(),
            ));
        }


        let check_status = check_account_status_active_user(
            &credential_result.clone().unwrap()
        );
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
        }

        if check_status.is_err() {
            return Err(
                ErrorResponse::bad_request(
                    1002,
                    "Email belum terdaftar".to_string(),
                )
            );
        }

        let user_credential = check_status.unwrap();

        if user_credential.auth_provider != AuthProvider::Basic {
            return Err(
                ErrorResponse::bad_request(
                    1003,
                    "Email sudah digunakan akun lain".to_string(),
                )
            );
        }
        return Ok(credential_result.unwrap());
    }

    pub async fn create_user_verification(
        &self,
        user: Model,
    ) -> Result<user_verification::Model, ErrorResponse> {
        let verification = create_user_verification(user);

        let saved = verification.insert(&self.db).await;

        if saved.is_err() {
            println!("Error create verification {}", saved.err().unwrap().to_string());
            return Err(ErrorResponse::bad_request(1002, "Kami mengalami kendala dalam menghubungi server [1]".to_string()));
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
        let redis_key = RedisUtil::new(verification_id)
            .create_key_otp_sign_in();

        let saved: Result<String, redis::RedisError> = connection
            .unwrap()
            .hset_multiple(redis_key.clone(), &[
                (common::constant::REDIS_KEY_OTP, otp),
                (common::constant::REDIS_KEY_USER_ID, user_id),
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(redis_key.clone(), common::constant::TTL_OTP);

        if saved.is_err() {
            return Err(ErrorResponse::unauthorized(
                saved.unwrap_err().to_string())
            );
        }
        let data = OtpRedisModel {
            otp: otp.to_string(),
            user_id: user_id.to_string(),
            session_id: verification_id.to_string(),
        };
        Ok(data)
    }
    //end sign in email & password
    //verify otp sign in
    pub async fn get_otp_sign_in_from_redis(
        &self,
        user_verification_id: &str,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        //obtain connection redis from main.rs via AppState
        let redis_connection = self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        //create key for otp sign in
        // return  (dev):otp:sign-in:(uuid)
        let redis_key = RedisUtil::new(user_verification_id)
            .create_key_otp_sign_in();

        let session_redis: RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(redis_key.clone());
        if session_redis.is_err() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa [1]".to_string()));
        }
        let redis = session_redis.unwrap();
        let otp = redis.get(common::constant::REDIS_KEY_OTP);
        let user_id = redis.get(common::constant::REDIS_KEY_USER_ID);
        if otp.is_none() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa [2]".to_string()));
        }
        let data = OtpRedisModel {
            user_id: user_id.unwrap().to_string(),
            otp: otp.unwrap().to_string(),
            session_id: user_verification_id.to_string(),
        };

        Ok(data)
    }
    pub async fn get_user_credential(
        &self,
        user_id: String,
    ) -> Result<(Model, Vec<user_profile::Model>), ErrorResponse> {
        let user = user_credential::Entity::find_by_id(user_id.clone())
            .one(&self.db)
            .await;
        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [1]".to_string()));
        }
        let credential = user.unwrap();
        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [2]".to_string()));
        }

        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_id.clone()))
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        Ok((credential.unwrap(), profile))
    }

    //end verify otp sign in

    //sign in google
    pub async fn get_user_by_google(
        &self,
        google_credential: common::jwt::Payload,
    ) -> Result<(user_credential::Model, Vec<user_profile::Model>), ErrorResponse> {
        let user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(google_credential.email.clone()))
            .one(&self.db).await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [3]".to_string()));
        }
        let credential_result = user.unwrap();

        let user: Result<Model, ErrorResponse> = match credential_result {
            None => {
                let uuid = Uuid::new_v4();
                let current_date = chrono::Utc::now().naive_local();
                let prepare_data = user_credential::ActiveModel {
                    id: Set(uuid.to_string()),
                    email: Set(google_credential.email.to_string()),
                    full_name: Set(google_credential.given_name.to_string()),
                    password: Set("n/a".to_string()),
                    status: Set(UserStatus::Active),
                    auth_provider: Set(AuthProvider::Google),
                    created_at: Set(current_date),
                    updated_at: Set(current_date),
                    deleted: Set(false),
                    ..Default::default()
                };
                let saved_data = prepare_data.insert(&self.db).await;
                if saved_data.is_err() {
                    return Err(ErrorResponse::unauthorized("".to_string()));
                }
                return Ok((saved_data.unwrap(), vec![]));
            }
            Some(ref user_credential) => Ok(user_credential.clone())
        };

        let check_status = check_account_status_active_user(
            &user.unwrap()
        );
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
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

        if user_credential.auth_provider != AuthProvider::Google {
            return Err(
                ErrorResponse::bad_request(
                    1003,
                    "Akun sudah digunakan".to_string(),
                )
            );
        }
        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_credential.id.clone()))
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        return Ok((credential_result.unwrap(), profile));
    }

    //end sign in google

    pub async fn save_user_session_to_redis(
        &self,
        user: &UserCredential,
    ) -> Result<SessionRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        if connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let session_key = RedisUtil::new(user.id.as_str())
            .create_key_session_sign_in();


        let generate_token = encode(user.id.clone());
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
