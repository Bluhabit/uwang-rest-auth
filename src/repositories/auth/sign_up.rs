use std::collections::HashMap;

use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, QueryFilter};
use sea_orm::ActiveValue::Set;
use serde_json::Value;
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::mail::email;
use crate::common::otp_generator::generate_otp;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::entity::{user_credential, user_profile};
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::Model;
use crate::models::user::UserCredentialResponse;

#[derive(Debug, Clone)]
pub struct SignUpRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignUpRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignUpRepository {
            db: state.db,
            cache: state.cache,
        }
    }
    /// == sign up email & password
    pub async fn sign_up_by_email(&mut self, email: &str) -> Result<String, ErrorResponse> {
        let data = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db).await;
        if data.is_ok() {
            let user = data.unwrap();
            if user.is_some() {
                let user = user.unwrap();
                if user.status != UserStatus::WaitingConfirmation {
                    return Err(ErrorResponse::unauthorized("Email sudah digunakan".to_string()));
                }

                let send_otp = self.create_and_save_otp(user).await;
                if send_otp.is_err() {
                    return Err(send_otp.unwrap_err());
                }
                return Ok(send_otp.unwrap());
            }
        }


        let current_date = chrono::Utc::now().naive_local();

        let uuid = Uuid::new_v4();
        let prepare_data = user_credential::ActiveModel {
            id: Set(uuid.to_string()),
            email: Set(email.to_string()),
            full_name: Set("n/a".to_string()),
            username: Set("n/a".to_string()),
            password: Set("n/a".to_string()),
            status: Set(UserStatus::WaitingConfirmation),
            auth_provider: Set(AuthProvider::Basic),
            created_at: Set(current_date),
            updated_at: Set(current_date),
            deleted: Set(false),
            ..Default::default()
        };
        let saved_data = prepare_data.insert(&self.db).await;
        if saved_data.is_err() {
            return Err(ErrorResponse::unauthorized("".to_string()));
        }
        let send_otp = self.create_and_save_otp(saved_data.unwrap()).await;
        if send_otp.is_err() {
            return Err(send_otp.unwrap_err());
        }
        Ok(send_otp.unwrap())
    }
    /// == end sign up email & password
    /// == send otp
    async fn create_and_save_otp(&mut self, user: Model) -> Result<String, ErrorResponse> {
        let session_id = Uuid::new_v4();
        let redis_util = RedisUtil::new(session_id.clone().to_string().as_str());
        let redis_key = redis_util.create_key_otp_sign_up();
        let generate_otp = generate_otp();
        let redis_connection = self.cache.get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal menghubungi sumber data.".to_string()));
        }
        let _: Result<String, redis::RedisError> = redis_connection
            .unwrap()
            .hset_multiple(redis_key.clone(), &[
                (common::constant::REDIS_KEY_OTP, generate_otp.as_str()),
                (common::constant::REDIS_KEY_USER_ID, user.id.as_str()),
                (common::constant::REDIS_KEY_OTP_ATTEMPT, "0")
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(redis_key.clone(), common::constant::TTL_OTP_SIGN_IN);

        let email = email::Email::new(
            user.email.clone(),
            user.full_name.clone(),
        );

        let _ = email.send_otp_sign_up_basic(
            serde_json::json!({

            })
        ).await;

        Ok(session_id.to_string())
    }
    /// == end send otp
    pub async fn verify_otp_sign_up(&self, session_id: &str, request_otp: &str) -> Result<Option<Value>, ErrorResponse> {
        let redis_connection = &self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }

        let redis_key = RedisUtil::new(session_id)
            .create_key_otp_sign_up();

        let redis_connection = self.cache.get_connection();
        let session_redis: RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(redis_key.clone());
        if session_redis.is_err() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa [1]".to_string()));
        }
        let _current_date = chrono::Utc::now().naive_local();
        let default_string = String::from("");
        let redis = session_redis.unwrap();

        let otp = redis.get(common::constant::REDIS_KEY_OTP)
            .unwrap_or(&default_string)
            .as_str();
        let user_id = redis.get(common::constant::REDIS_KEY_USER_ID)
            .unwrap_or(&default_string)
            .as_str();
        let mut attempt_otp_sign_up: i16 = redis.get(common::constant::REDIS_KEY_OTP_ATTEMPT)
            .unwrap_or(&"0".to_string())
            .parse()
            .unwrap_or(0);

        let user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [1]".to_string()));
        }
        let credential = user.unwrap();
        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [2]".to_string()));
        }
        let credential = credential.unwrap();

        if attempt_otp_sign_up >= 4 {
            return Err(ErrorResponse::bad_request(1001, "Kamu sudah mencoba otp 3 kali.".to_string()));
        }

        if !request_otp.eq(otp) {
            attempt_otp_sign_up = attempt_otp_sign_up + 1;
            let redis_connection = self.cache.get_connection();
            let _: RedisResult<String> = redis_connection
                .unwrap()
                .hset_multiple(
                    redis_key, &[(common::constant::REDIS_KEY_OTP_ATTEMPT, attempt_otp_sign_up)],
                );
            return Err(ErrorResponse::unauthorized("Kode OTP Salah.".to_string()));
        }

        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_id))
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        let save_session = common::utils::save_user_session_to_redis(
            self.cache.get_connection().unwrap(),
            &redis_key,
            &credential,
        ).await;


        let credential_response = UserCredentialResponse::from_credential_with_profile(
            credential,
            profile,
        );
        let save_session = save_session.unwrap();

        let _: RedisResult<String> = self.cache.get_connection()
            .unwrap()
            .del(redis_key);

        let token = save_session.token;

        Ok(Some(
            serde_json::json!({
                "token":token,
                "user":credential_response
            })
        ))
    }
    /// == end verify otp
    /// == resend otp
    pub async fn resend_otp_sign_up_basic(
        &self,
        session_id: &str,
    ) -> Result<String, ErrorResponse> {
        let redis_connection = &self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        let redis_util = RedisUtil::new(session_id);
        let redis_key = redis_util.create_key_otp_sign_up();
        let generate_otp = generate_otp();

        let redis_connection = self.cache
            .get_connection();
        let get_session: RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(redis_key.clone());

        if get_session.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Gagal membuat ulang otp, sesi tidak valid.".to_string(),
            ));
        }
        let default_string = String::from("");
        let get_session = get_session.unwrap();
        let user_id = get_session.get(common::constant::REDIS_KEY_USER_ID)
            .unwrap_or(&default_string);
        let find_user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;
        if find_user.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Gagal membuat ulang otp, sesi tidak valid.".to_string(),
            ));
        }
        let user = find_user.unwrap();

        if user.is_none() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Gagal membuat ulang otp, sesi tidak valid.".to_string(),
            ));
        }
        let user = user.unwrap();

        let redis_connection = self.cache
            .get_connection();
        let _: RedisResult<String> = redis_connection
            .unwrap()
            .hset_multiple(
                redis_key, &[
                    (common::constant::REDIS_KEY_OTP, generate_otp.as_str()),
                    (common::constant::REDIS_KEY_OTP_ATTEMPT, "0")
                ],
            );

        let email = email::Email::new(
            user.email.clone(),
            user.full_name.clone(),
        );

        let _ = email.send_otp_sign_in_basic(
            serde_json::json!({

            })
        ).await;
        Ok(session_id.to_string())
    }
}
