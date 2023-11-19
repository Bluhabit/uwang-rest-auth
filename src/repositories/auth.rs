use std::collections::HashMap;
use std::default::Default as time_default;

use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::ActiveValue::Set;
use sea_orm::{
    ActiveModelTrait, ColumnTrait, DatabaseConnection, DbErr, EntityTrait, PaginatorTrait,
    QueryFilter,
};

use crate::common::jwt::encode;
use crate::entity::sea_orm_active_enums::{AuthProvider, Status, VerificationType};
use crate::entity::{user_credential, user_verification};
use crate::models::auth::{SessionModel, VerifyOtpRequest};
use crate::AppState;

#[derive(Debug, Clone)]
pub struct AuthRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl AuthRepository {
    pub fn init(app_state: &AppState) -> AuthRepository {
        let state = app_state.clone();
        AuthRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    pub async fn set_user_session(
        &mut self,
        user: &user_credential::Model,
    ) -> Option<SessionModel> {
        let token: Option<String> = encode(user.id.to_string());
        if token.is_none() {
            return None;
        }
        let session = SessionModel {
            session_id: user.id.to_string(),
            full_name: user.full_name.to_string(),
            email: user.email.to_string(),
            token: token.unwrap().to_string(),
            permission: vec![],
        };

        let session_id: String = format!("sessions:{}:profile", user.id.to_string());
        let result: Result<String, redis::RedisError> =
            self.cache.get_connection().unwrap().hset_multiple(
                session_id.clone(),
                &[
                    ("session_id", &session.session_id.to_string()),
                    ("full_name", &session.full_name.to_string()),
                    ("email", &session.email.to_string()),
                    ("token", &session.token.to_string()),
                ],
            );
        let _: RedisResult<_> = self.cache.expire::<String, String>(
            session_id,
            1000,
        );
        if result.is_err() {
            return None;
        }
        Some(session)
    }

    pub async fn get_user_session(&self, session_id: String) -> Option<HashMap<String, String>> {
        let connection = self.cache.get_connection();
        if connection.is_err() {
            return None;
        }
        let result: RedisResult<HashMap<String, String>> = connection
            .unwrap()
            .hgetall(format!("session:{}", session_id));
        if result.is_err() {
            return None;
        }
        Some(result.unwrap())
    }

    pub async fn get_verification_otp_from_cache(
        &self,
        req: &VerifyOtpRequest,
    ) -> Option<bool> {
        let connection = self.cache.get_connection();
        if connection.is_err() {
            return None;
        }
        let get_otp_verification: RedisResult<String> = connection
            .unwrap()
            .get(format!("otp:{}", req.session_id.to_string()));
        if get_otp_verification.is_err() {
            return None;
        }

        Some(get_otp_verification.unwrap().eq(req.code.as_str()))
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

    pub async fn get_user_by_email(&self, email: &String) -> Option<user_credential::Model> {
        let find_user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db)
            .await;
        match find_user {
            Ok(user_credential) => {
                if user_credential.is_none() {
                    return None;
                }
                return Some(user_credential.unwrap());
            }
            Err(_) => None,
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
            status: Set(Status::WaitingConfirmation),
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

        let uuid = uuid::Uuid::new_v4();
        let verification = user_verification::ActiveModel {
            id: Set(uuid.to_string()),
            code: Set("1234".to_string()),
            verification_type: Set(VerificationType::Otp),
            user_id: Set(Some(user.id.to_string())),
            created_at: Set(current_date),
            updated_at: Set(current_date),
            deleted: Set(false),
            ..Default::default()
        };

        verification.insert(&self.db).await
    }
}
