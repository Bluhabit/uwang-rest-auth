use sea_orm::prelude::DateTime;
use serde::{Deserialize, Serialize};
use validator::Validate;
use crate::entity::prelude::UserProfile;

use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::Model as UserCredential;
use crate::entity::user_profile;

#[derive(Serialize, Deserialize)]
pub struct UserInfoRequest {
    pub name: String,
    pub value: String,
}

#[derive(Serialize, Deserialize)]
pub struct AddUserInfoRequest {
    pub user_id: String,
    pub user_info: Vec<UserInfoRequest>,
}

#[derive(Serialize, Deserialize,Validate)]
pub struct CompleteProfileRequest {
    pub date_of_birth: String,
    pub username:String,
    pub avatar:String,
    pub personal_preferences:Vec<String>
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UserCredentialResponse {
    pub id: String,
    pub email: String,
    pub full_name: String,
    pub status: UserStatus,
    pub auth_provider: AuthProvider,
    pub profile: Vec<user_profile::Model>,
    pub created_at: DateTime,
    pub updated_at: DateTime,
    pub deleted: bool,
}

impl UserCredentialResponse {
    pub fn from_credential(
        user_credential: UserCredential
    ) -> Self {
        UserCredentialResponse {
            id: user_credential.id,
            email: user_credential.email,
            full_name: user_credential.full_name,
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
        UserCredentialResponse {
            id: user_credential.id,
            email: user_credential.email,
            full_name: user_credential.full_name,
            status: user_credential.status,
            auth_provider: user_credential.auth_provider,
            profile,
            created_at: user_credential.created_at,
            updated_at: user_credential.updated_at,
            deleted: user_credential.deleted,
        }
    }
}