use std::collections::HashMap;

use bcrypt::{DEFAULT_COST, hash};
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter};
use sea_orm::ActiveValue::Set;

use crate::{AppState, common};
use crate::common::jwt::encode;
use crate::common::mail::email;
use crate::common::otp_generator::generate_otp;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::{check_account_user_status_active, create_session_redis_from_user};
use crate::entity::sea_orm_active_enums::UserStatus;
use crate::entity::user_credential;

#[derive(Debug, Clone)]
pub struct ForgotPasswordRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl ForgotPasswordRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        ForgotPasswordRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    /// == forgot password
    pub async fn forgot_password(
        &mut self,
        email: &str,
    ) -> Result<String, ErrorResponse> {
        let redis_connection = &self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        let find_user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db)
            .await;

        if find_user.is_err() {
            return Err(ErrorResponse::bad_request(200, "Akun tidak ditemukan.".to_string()));
        }
        let user = find_user.unwrap();
        if user.is_none() {
            return Err(ErrorResponse::bad_request(200, "Akun tidak ditemukan.".to_string()));
        }
        let check_user = user.clone().unwrap();
        let check_status = check_account_user_status_active(&check_user);
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
        }
        let user = user.unwrap();

        let uuid = uuid::Uuid::new_v4();
        let session_id = uuid.to_string();
        let redis_util = RedisUtil::new(session_id.clone().as_str());
        let redis_key = redis_util.create_key_otp_forgot_password();

        let generate_otp = generate_otp();
        let redis_connection = self.cache.get_connection();
        let saved_session_otp: Result<String, redis::RedisError> = redis_connection
            .unwrap()
            .hset_multiple(redis_key.clone(), &[
                (common::constant::REDIS_KEY_OTP, generate_otp.clone().as_str()),
                (common::constant::REDIS_KEY_USER_ID, user.clone().id.as_str()),
                (common::constant::REDIS_KEY_OTP_ATTEMPT, "0")
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(redis_key.clone(), common::constant::TTL_OTP_FORGOT_PASSWORD);

        if saved_session_otp.is_err() {
            return Err(ErrorResponse::unauthorized(
                saved_session_otp.unwrap_err().to_string())
            );
        }

        //sending email
        let email = email::Email::new(
            user.email.clone(),
            user.full_name.clone(),
        );

        let _ = email.send_otp_sign_in_basic(
            serde_json::json!({
                "otp": generate_otp
            })
        ).await;

        Ok(session_id)
    }
    /// == end forgot password
    /// == verify otp
    pub async fn verify_otp_forgot_password(
        &mut self,
        session_id: &str,
        request_otp:&str
    ) ->Result<String,ErrorResponse>{
        let redis_connection = &self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }

        let redis_key = RedisUtil::new(session_id)
            .create_key_otp_forgot_password();

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

        let user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;
        println!("err {:?}",redis);

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa.".to_string()));
        }
        let credential = user.unwrap();
        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa.".to_string()));
        }
        let credential = credential.unwrap();

        let check_user = credential.clone();
        let check_status = check_account_user_status_active(&check_user);
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
        }

        if !request_otp.eq(otp) {
            return Err(ErrorResponse::unauthorized("Kode OTP Salah.".to_string()));
        }

        let credential = credential;
        let uuid = uuid::Uuid::new_v4();
        let redis_util = RedisUtil::new(uuid.clone().to_string().as_str());
        let redis_key_session = redis_util.create_key_session_forgot_password();

        let generate_token = encode(credential.id.clone());
        if generate_token.is_none() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let redis_connection = self.cache.get_connection();
        let _: Result<String, redis::RedisError> = redis_connection
            .unwrap()
            .hset_multiple(
                redis_key_session.clone(),
                &*create_session_redis_from_user(
                    credential.clone(),
                    generate_token
                        .clone()
                        .unwrap(),
                ),
            );
        let _: RedisResult<_> = self.cache
            .expire::<String, String>(redis_key_session.clone(), common::constant::TTL_SESSION_FORGOT_PASSWORD);


        let _: RedisResult<String> = self.cache.get_connection()
            .unwrap()
            .del(redis_key);

        Ok(uuid.to_string())
    }
    /// == end verify otp
    /// == resend otp forgot password
    pub async fn resend_otp_forgot_password(
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
        let redis_key = redis_util.create_key_otp_forgot_password();
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
                    (common::constant::REDIS_KEY_OTP, generate_otp.clone().as_str()),
                    (common::constant::REDIS_KEY_OTP_ATTEMPT, "0")
                ],
            );
        let email = email::Email::new(
            user.email.clone(),
            user.full_name.clone(),
        );

        let _ = email.send_otp_forgot_password(
            serde_json::json!({
                "otp":generate_otp
            })
        ).await;
        Ok(session_id.to_string())
    }
    /// == end resend forgot password
    /// == set new password
    pub async fn set_new_password(
        &mut self,
        session_id: &str,
        new_password: &str
    ) -> Result<String,ErrorResponse>{
        let redis_connection = self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        let redis_util = RedisUtil::new(session_id);
        let redis_key = redis_util.create_key_session_forgot_password();

        let redis_session:RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(redis_key.clone().as_str());
        if redis_session.is_err(){
            return Err(ErrorResponse::bad_request(200,"Sesi tidak valid.".to_string()));
        }
        let default_string = String::from("");
        let redis_session = redis_session.unwrap();
        let user_id = redis_session.get(common::constant::REDIS_KEY_USER_ID)
            .unwrap_or(&default_string);

        let find_user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;
        if find_user.is_err(){
            return Err(ErrorResponse::bad_request(400,"Tidak dapat menemukan akun Kamu.".to_string()));
        }
        let find_user = find_user.unwrap();
        if find_user.is_none(){
            return Err(ErrorResponse::bad_request(400,"Tidak dapat menemukan akun Kamu.".to_string()));
        }
        let user = find_user.unwrap();


        let mut active_model = user.clone().into_active_model();
        let hash_password = hash(&new_password.to_string(), DEFAULT_COST);
        if hash_password.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal merubah password".to_string()));
        }
        active_model.password = Set(hash_password.unwrap());

        let updated_data = active_model.update(&self.db)
            .await;
        if updated_data.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal merubah password [2]".to_string()));
        }

        Ok(session_id.to_string())
    }

}