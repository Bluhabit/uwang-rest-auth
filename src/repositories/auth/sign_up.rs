use std::default::Default as time_default;

use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::{
    ActiveModelTrait, ColumnTrait, DatabaseConnection, DbErr, EntityTrait, PaginatorTrait,
    QueryFilter,
};
use sea_orm::ActiveValue::Set;

use crate::AppState;
use crate::common::otp_generator::generate_otp;
use crate::entity::{user_credential, user_verification};
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus, VerificationType};

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


    pub async fn is_email_used(&self, email: &String) -> bool {
        let find_email = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .count(&self.db)
            .await;
        match find_email {
            Ok(account) => account > 0,
            Err(_) => false,
        }
    }

    pub async fn insert_user_basic(
        &self,
        email: &String,
        password: &String,
        full_name: &String,
    ) -> Result<user_credential::Model, DbErr> {
        let current_date = chrono::DateTime::<FixedOffset>::default().naive_local();

        let uuid = uuid::Uuid::new_v4();
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

        prepare_data.insert(&self.db).await
    }

    pub async fn create_user_verification(
        &self,
        user: &user_credential::Model,
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

    pub async fn save_otp_sign_up_to_redis(
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
