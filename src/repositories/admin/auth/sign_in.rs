use redis::Client;
use sea_orm::{
    ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, ModelTrait, QueryFilter,
};
use serde_json::Value;
use uuid::Uuid;

use crate::common::mail::email;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::check_account_user_status_active;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::Model;
use crate::entity::{admin, admin_role, system_access, user_credential, user_profile};
use crate::models::admin::auth::{RolesResponse, SignInAdminRequest, SignInAdminResponse};
use crate::{common, AppState};

#[derive(Debug, Clone)]
pub struct SignInAdminRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInAdminRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignInAdminRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    ///== sign in email & password
    pub async fn sign_in_by_email(
        &mut self,
        email: &str,
        password: &str,
    ) -> Result<Option<Value>, ErrorResponse> {
        let redis_connection = &self.cache.get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        let user_credential = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db)
            .await;

        if user_credential.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Tidak dapat menemukan akun".to_string(),
            ));
        }

        let data_user = user_credential.unwrap();
        if data_user.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Tidak dapat menemukan akun".to_string(),
            ));
        }
        let data_user = data_user.unwrap();

        let admin = data_user.find_related(admin::Entity).one(&self.db).await;

        if admin.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Akun Anda tidak terdaftar sebagai admin".to_string(),
            ));
        }

        let data_admin = admin.unwrap();
        if data_admin.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Akun Anda tidak terdaftar sebagai admin".to_string(),
            ));
        }
        let data_admin = data_admin.unwrap();
        let admin_id = data_admin.clone().id;
        let verify_password = bcrypt::verify(password, &data_admin.password);
        if verify_password.is_err() {
            return Err(ErrorResponse::bad_request(
                401,
                "Email atau password salah.".to_string(),
            ));
        }

        let role = admin_role::Entity::find()
            .filter(admin_role::Column::AdminId.eq(admin_id))
            .find_also_related(system_access::Entity)
            .all(&self.db)
            .await
            .unwrap_or(vec![]);



        // Inisialisasi role_response di luar blok
        let mut role_response = RolesResponse {
            role_id: Default::default(),    
            role: Default::default(),       
            permission: Default::default(), 
        };

        if let Some((first_admin_role, system_access)) = role.first().cloned() {
            // Mengisi nilai role_response di dalam blok
            role_response = RolesResponse {
                role_id: first_admin_role.id.to_string(),
                role: system_access.clone().map(|sa| sa.name).unwrap_or_default(),
                permission: system_access.clone().map(|sa| sa.permission).unwrap_or_default(),
            };
        }
        let profile = user_profile::Entity::find()
        .filter(user_profile::Column::UserId.eq(data_user.id.clone()))
        .all(&self.db)
        .await
        .unwrap_or(vec![]);
        let user_credential = SignInAdminResponse::from_credential_with_profile(data_user,profile);

        // role_response dapat diakses di sini
        Ok(Some(serde_json::json!({
            "user": user_credential,
            "role": role_response,
        })))
    }
}
