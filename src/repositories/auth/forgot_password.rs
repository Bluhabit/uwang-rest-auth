use std::os::linux::raw::stat;
use redis::{Client, Commands};
use sea_orm::{ColumnTrait, DatabaseConnection, EntityTrait, QueryFilter};
use sea_orm::ColumnType::Uuid;
use serde_json::to_string;
use crate::AppState;
use crate::common::mail::email::Email;
use crate::common::otp_generator::generate_otp;
use crate::common::redis_ext::RedisUtil;
use crate::entity::prelude::UserCredential;
use crate::entity::user_credential;

#[derive(Debug, Clone)]
pub struct ForgotPasswordRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl ForgotPasswordRepository {
    pub fn init(app_state: &AppState) -> ForgotPasswordRepository {
        let state = app_state.clone();
        ForgotPasswordRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    pub async fn get_user_credential_by_email(&self, email: &str) -> Result<UserCredential, String> {
        let user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db).await;

        if user.is_err() {
            return Err("Akun tidak ditemukan".to_string());
        }
        let credential_result = user.unwrap();

        if credential_result.is_none() {
            return Err("Akun tidak ditemukan".to_string());
        }
        return Ok(credential_result.unwrap());
    }

    pub async fn save_otp_forgot_password_to_redis(&mut self, user: &UserCredential) -> Result<String, String> {
        let otp = generate_otp();
        let ttl = 60;

        let verification_id = Uuid::new_v4().to_string();
        let otp_key = RedisUtil::new(&verification_id).create_key_otp_forgot_password();

        self.cache.set_ex(&otp_key, &otp, ttl).await
            .map_err(|_| {
                "Gagal menyimpan OTP".to_string()
            })?;
        Ok(otp_key)
    }

    // pub async fn forgot_password(&self, email: &str) -> Result<String, String> {
    //     let user = self.get_user_credential_by_email(email).await?;
    //
    //     // OTP Generate
    //     let otp = generate_otp();
    //     let ttl = 60;
    //
    //     // OTP Save
    //     self.save_otp_to_redis(&user, &otp, ttl).await?;
    //
    //     Ok("OTP sent successfully".to_string())
    // }
}