use sea_orm::prelude::DateTime;
use serde::{Deserialize, Serialize};
use validator::Validate;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_profile;
#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SignInAdminRequest {
    #[validate(email(code = "regex", message = "Email tidak boleh kosong."))]
    pub email: String,
    #[validate(length(min = 6, message = "Password tidak boleh kosong,minimal 6 karakter."))]
    pub password: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SignInAdminResponse {
    pub email: String,
    pub full_name: String,
    pub username: String,
    pub gender: String,
    pub date_of_birth: String,
    pub status: UserStatus,
    pub auth_provider: AuthProvider,
    pub profile: Vec<user_profile::Model>,
    pub roles:Vec<RolesResponse>,
    pub created_at: DateTime,
    pub updated_at: DateTime,
    pub deleted: bool,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RolesResponse {
    pub role_id:String,
    pub role:String,
    pub permission:String,
}
