use std::collections::HashMap;
use std::default::Default as time_default;

use bcrypt::{DEFAULT_COST, hash};
use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter};
use sea_orm::ActiveValue::Set;
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::jwt::encode;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::{create_session_from_user, create_session_redis_from_user};
use crate::entity::{user_credential, user_verification};
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::models::auth::{OtpRedisModel, SessionRedisModel};
use crate::models::utils::create_user_verification;

#[derive(Debug, Clone)]
pub struct SignUpRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignUpRepository {
    pub fn init(app_state: &AppState) -> SignUpRepository {
        let state = app_state.clone();
        SignUpRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    //sign up email & passowrd
    pub async fn create_user_credential(
        &self,
        email: &String,
        password: &String,
        full_name: &String,
    ) -> Result<user_credential::Model, ErrorResponse> {
        let data = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db).await;
        if data.is_ok() {
            if data.unwrap().is_some() {
                return Err(ErrorResponse::unauthorized("Email sudah digunakan".to_string()));
            }
        }
        let current_date = chrono::DateTime::<FixedOffset>::default().naive_local();

        let uuid = uuid::Uuid::new_v4();
        let hash_password = hash(&password.to_string(), DEFAULT_COST);
        if hash_password.is_err() {
            return Err(ErrorResponse::bad_request(
                1000,
                "Gagal membuat akun ".to_string(),
            ));
        }
        let prepare_data = user_credential::ActiveModel {
            id: Set(uuid.to_string()),
            email: Set(email.to_string()),
            full_name: Set(full_name.to_string()),
            password: Set(password.to_string()),
            status: Set(UserStatus::WaitingConfirmation),
            auth_provider: Set(AuthProvider::Basic),
            created_at: Set(current_date),
            updated_at: Set(current_date),
            deleted: Set(false),
            ..Default::default()
        };
        let saved_data = prepare_data.insert(&self.db).await;
        if saved_data.is_err() {
            println!("Database error {}", saved_data.err().unwrap().to_string());
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        Ok(saved_data.unwrap())
    }

    pub async fn create_user_verification(
        &self,
        user: user_credential::Model,
    ) -> Result<user_verification::Model, ErrorResponse> {
        let verification = create_user_verification(user);

        let user_verification = verification.insert(&self.db).await;
        if user_verification.is_err() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        Ok(user_verification.unwrap())
    }

    pub async fn save_otp_sign_up_to_redis(
        &mut self,
        verification_id: &str,
        otp: &str,
        user_id: &str,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        let redis_key = RedisUtil::new(verification_id)
            .create_key_otp_sign_up();

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
    //end sign up email & password
    // verify otp email & password
    pub async fn get_otp_sign_up_from_redis(
        &mut self,
        session_id: String,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        let redis_connection = self
            .cache.get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                400,
                "Kami mengalami masalah dalam mengkonfirmasi otp, silahkan coba beberapa saat lagi".to_string(),
            ));
        }

        let session_key = RedisUtil::new(session_id.as_str())
            .create_key_otp_sign_up();

        let session: RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(session_key.clone());
        if session.is_err() {
            return Err(ErrorResponse::bad_request(
                400,
                "Kami mengalami masalah dalam mengkonfirmasi otp, silahkan coba beberapa saat lagi".to_string(),
            ));
        }
        let redis = session.unwrap();
        let otp = redis.get(common::constant::REDIS_KEY_OTP);
        let user_id = redis.get(common::constant::REDIS_KEY_USER_ID);

        if otp.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Otp tidak sesuai atau sudah expired".to_string()
            ));
        }

        let data = OtpRedisModel {
            user_id: user_id.unwrap().to_string(),
            otp: otp.unwrap().to_string(),
            session_id: session_key,
        };
        Ok(data)
    }

    pub async fn update_verification_user_status(
        &self,
        user_id: String,
    ) -> Result<user_credential::Model, ErrorResponse> {
        let credential = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;

        if credential.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Otp tidak sesuai atau sudah expired".to_string()
            ));
        }
        let user_credential = credential.unwrap();
        if user_credential.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Otp tidak sesuai atau sudah expired".to_string()
            ));
        }

        let mut updated_credential: user_credential::ActiveModel = user_credential
            .unwrap()
            .into_active_model();
        updated_credential.status = Set(UserStatus::Active);

        let updated_result = updated_credential.update(&self.db).await;
        if updated_result.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Otp tidak sesuai atau sudah expired".to_string()
            ));
        }
        Ok(updated_result.unwrap())
    }

    //end verify otp email & password
    pub async fn save_user_session_to_redis(
        &self,
        user: &user_credential::Model,
    ) -> Result<SessionRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        if connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let session_id = Uuid::new_v4();
        let session_key = RedisUtil::new(session_id.to_string().as_str())
            .create_key_session_sign_in();


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