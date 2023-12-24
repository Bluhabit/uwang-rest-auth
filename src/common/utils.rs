use validator::ValidationErrors;
use crate::common;
use crate::common::response::ErrorResponse;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential;
use crate::models::auth::SessionRedisModel;

pub fn get_readable_validation_message(
    err: Option<ValidationErrors>
) -> String {
    match err {
        None => String::from(""),
        Some(validation) => validation.field_errors().into_iter()
            .map(|(field, b)| {
                let message: String = b.into_iter().map(|er| {
                    let message = match er.clone().message {
                        Some(val) => val.to_string(),
                        None => String::from("<no message>")
                    };
                    return format!("{}, ", message);
                }).collect();
                return message;
            }).collect()
    }
}

pub fn check_account_status_active_user(
    credential: &user_credential::Model
) -> Result<user_credential::Model, ErrorResponse> {
    match credential.status {
        UserStatus::Active => Ok(credential.clone()),
        UserStatus::Inactive => Err(ErrorResponse::unauthorized("Akun Anda tidak aktif".to_string())),
        UserStatus::Suspended => Err(ErrorResponse::unauthorized("Akun Anda ditangguhkan, anda tidak dapat melanjutkan proses ini".to_string())),
        UserStatus::WaitingConfirmation => {
            if credential.auth_provider == AuthProvider::Google {
                return Ok(credential.clone());
            }
            return Err(ErrorResponse::unauthorized("Akun Anda belum terkonfirmasi, silahkan cek email untuk mengkonfirmasi".to_string()));
        }
    }
}


pub fn create_session_redis_from_user(
    user: user_credential::Model,
    token: String,
) -> Vec<(String, String)> {
    return vec![
        (common::constant::REDIS_KEY_USER_ID.to_string(), user.id),
        (common::constant::REDIS_KEY_EMAIL.to_string(), user.email),
        (common::constant::REDIS_KEY_FULL_NAME.to_string(), user.full_name),
        (common::constant::REDIS_KEY_SESSION_ID.to_string(), token),
    ];
}

pub fn create_session_from_user(
    user: user_credential::Model,
    token: String,
) -> SessionRedisModel {
    SessionRedisModel {
        user_id: user.id.to_string(),
        full_name: user.full_name.to_string(),
        email: user.email.to_string(),
        token,
    }
}
