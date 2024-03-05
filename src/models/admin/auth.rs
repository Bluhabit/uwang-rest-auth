use sea_orm::ActiveEnum;
use sea_orm::prelude::DateTime;
use serde::{Deserialize, Serialize};
use validator::Validate;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::Model as UserCredential;
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
    pub created_at: DateTime,
    pub updated_at: DateTime,
    pub deleted: bool,
}
impl SignInAdminResponse {
    pub fn from_credential(user_credential: UserCredential) -> Self {
        let dob = match user_credential.date_of_birth {
            Some(value) => value.format("%d-%m-%Y").to_string(),
            None => "".to_string(),
        };
        let gender = match user_credential.gender {
            Some(value) => value.to_value(),
            None => "".to_string(),
        };

        SignInAdminResponse {
            email: user_credential.email,
            full_name: user_credential.full_name,
            username: user_credential.username,
            date_of_birth: dob,
            gender,
            status: user_credential.status,
            auth_provider: user_credential.auth_provider,
            profile: vec![],
            created_at: user_credential.created_at,
            updated_at: user_credential.updated_at,
            deleted: user_credential.deleted,
        }
    }
    pub fn from_credential_with_profile(
        user_credential: UserCredential,
        profile: Vec<user_profile::Model>,
    ) -> Self {
        let dob = match user_credential.date_of_birth {
            Some(value) => value.format("%d-%m-%Y").to_string(),
            None => "".to_string(),
        };
        let gender = match user_credential.gender {
            Some(value) => value.to_value(),
            None => "".to_string(),
        };
        SignInAdminResponse {
            email: user_credential.email,
            full_name: user_credential.full_name,
            status: user_credential.status,
            username: user_credential.username,
            date_of_birth: dob,
            gender,
            auth_provider: user_credential.auth_provider,
            profile,
            created_at: user_credential.created_at,
            updated_at: user_credential.updated_at,
            deleted: user_credential.deleted,
        }
    }
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RolesResponse {
    pub role_id:String,
    pub role:String,
    pub permission:String,
}
