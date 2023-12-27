use std::collections::HashMap;

use bcrypt::{DEFAULT_COST, hash};
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter};
use sea_orm::ActiveValue::Set;

use crate::{AppState, common};
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::check_account_status_active_user;
use crate::entity::{user_credential, user_verification};
use crate::models::auth::OtpRedisModel;
use crate::models::utils::create_user_verification;

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

    //request forgot password
    pub async fn get_user_by_email(
        &self,
        email: String,
    ) -> Result<user_credential::Model, ErrorResponse> {
        let find_user_by_email = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db)
            .await;
        if find_user_by_email.is_err() {
            return Err(ErrorResponse::bad_request(400, "Akun dengan email tersebut tidak ditemukan [1]".to_string()));
        }
        let user = find_user_by_email.unwrap();
        if user.is_none() {
            return Err(ErrorResponse::bad_request(400, "Akun dengan email tersebut tidak ditemukan [2]".to_string()));
        }

        let check_status = check_account_status_active_user(&user.unwrap());
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
        }
        Ok(check_status.unwrap())
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

    pub async fn save_otp_forgot_password_to_redis(
        &mut self,
        verification_id: &str,
        otp: &str,
        user_id: &str,
    ) -> Result<OtpRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        let redis_key = RedisUtil::new(verification_id)
            .create_key_otp_forgot_password();

        let saved: Result<String, redis::RedisError> = connection
            .unwrap()
            .hset_multiple(redis_key.clone(), &[
                (common::constant::REDIS_KEY_OTP, otp),
                (common::constant::REDIS_KEY_USER_ID, user_id),
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(
                redis_key.clone(),
                common::constant::TTL_OTP_FORGOT_PASSWORD,
            );

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
    //end request forgot password

    //verify otp forgot password
    pub async fn get_otp_forgot_password_from_redis(
        &self,
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

    pub async fn get_user_credential(
        &self,
        user_id: String,
    ) -> Result<user_credential::Model, ErrorResponse> {
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
        Ok(credential.unwrap())
    }

    pub async fn save_session_forgot_password(
        &mut self,
        user: user_credential::Model,
    ) -> Result<String, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        let uuid = uuid::Uuid::new_v4();
        let redis_key = RedisUtil::new(uuid.to_string().as_str())
            .create_key_otp_sign_in();

        let saved: Result<String, redis::RedisError> = connection
            .unwrap()
            .set(redis_key.clone(), user.id);


        let _: RedisResult<_> = self.cache
            .expire::<String, String>(redis_key.clone(), common::constant::TTL_OTP_FORGOT_PASSWORD);

        if saved.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal menyimpan sesi".to_string()));
        }
        Ok(uuid.to_string())
    }
    //end verify otp forgot password
    //set new password
    pub async fn get_user_by_session(
        &self,
        session_id: String,
    ) -> Result<user_credential::Model, ErrorResponse> {
        let redis_connection = self.cache.get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal mengubungi server [1]".to_string()));
        }
        let mut connection = redis_connection.unwrap();
        let session_key = RedisUtil::new(session_id.as_str())
            .create_key_session_forgot_password();

        let user_id: RedisResult<String> = connection.get(session_key);
        if user_id.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal menghubungi server [2]".to_string()));
        }

        let get_user = user_credential::Entity::find_by_id(user_id.unwrap())
            .one(&self.db)
            .await;

        if get_user.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal mengubungi server [3]".to_string()));
        }
        let get_user_credential = get_user.unwrap();
        if get_user_credential.is_none() {
            return Err(ErrorResponse::bad_request(400, "Sesi sudah berakhir silahkan ulang ke tahap sebelumnya".to_string()));
        }
        let user = get_user_credential.unwrap();
        Ok(user)
    }

    pub async fn set_new_password(
        &self,
        user: user_credential::Model,
        new_password: &String,
    ) -> Result<String, ErrorResponse> {
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
        Ok(user.clone().full_name)
    }
    //end set password
}