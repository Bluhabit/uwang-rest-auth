use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, DbErr, EntityTrait, QueryFilter};
use sea_orm::ActiveValue::Set;

use crate::AppState;
use crate::common::otp_generator::generate_otp;
use crate::entity::{user_credential, user_verification};
use crate::entity::sea_orm_active_enums::VerificationType;
use crate::entity::user_credential::Model;

#[derive(Debug, Clone)]
pub struct SignInRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInRepository {
    pub fn init(app_state: &AppState) -> SignInRepository {
        let state = app_state.clone();
        SignInRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    pub async fn get_user_credential_by_email(&self, email: &str) -> Result<Model, String> {
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


    pub async fn create_user_verification(
        &self,
        user: &Model,
    ) -> Result<user_verification::Model, DbErr> {
        let current_date = chrono::DateTime::<FixedOffset>::default().naive_local();
        let otp = generate_otp();
        let uuid = uuid::Uuid::new_v4();

        let verification = user_verification::ActiveModel {
            id: Set(uuid.to_string()),
            code: Set(otp.clone().to_string()),
            verification_type: Set(VerificationType::Otp),
            user_id: Set(Some(user.id.to_string())),
            created_at: Set(current_date),
            updated_at: Set(current_date),
            deleted: Set(false),
            ..Default::default()
        };

        verification.insert(&self.db).await
    }

    pub async fn save_otp_sign_in_to_redis(
        &mut self,
        verification_id: &str,
        otp: &str,
    ) -> Result<String, String> {
        let connection = self.cache
            .get_connection();
        let session_id = format!("otp:{}", verification_id);

        let saved: Result<String, redis::RedisError> = connection.unwrap()
            .set(&session_id, otp);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(session_id.clone(), 60);

        if saved.is_err() {
            return Err(saved.unwrap_err().to_string());
        }
        Ok(session_id)
    }
}
