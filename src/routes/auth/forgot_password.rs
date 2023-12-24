use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::{AppState, ErrorResponse};
use crate::common::mail::email;
use crate::common::response::{BaseResponse};
use crate::common::utils::get_readable_validation_message;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::models::auth::ForgotPasswordRequest;
use crate::repositories::auth::forgot_password::ForgotPasswordRepository;

pub async fn forgot_password(
    state: web::Data<AppState>,
    body: web::Json<ForgotPasswordRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message))
    }

    let mut forgot_password_repository = ForgotPasswordRepository::init(&state);

    let find_user = forgot_password_repository
        .get_user_credential_by_email(&body.email).await;

    if find_user.is_err() {
        return Err(ErrorResponse::bad_request(
            1001,
            "Akun tidak ditemukan".to_string(),
        ));
    }

    let user_credential = find_user.unwrap();
    if user_credential.auth_provider != AuthProvider::Basic {
        return Err(ErrorResponse::bad_request(
            1003,
            "Email sudah digunakan oleh akun lain".to_string(),
        ));
    }

    match user_credential.status {
        UserStatus::Inactive | UserStatus::Suspended | UserStatus::WaitingConfirmation => {
            return Err(ErrorResponse::bad_request(
                1004,
                "Akun Anda tidak aktif".to_string(),
            ));
        },
        UserStatus::Active => {
            let otp_key = forgot_password_repository.save_otp_forgot_password_to_redis(&user_credential).await
                .map_err(|_| ErrorResponse::bad_request(1005, "Gagal menyimpan OTP".to_string()))?;

            if let Err(_) = forgot_password_repository.send_otp_email(&user_credential, &otp_key).await {
                return Err(ErrorResponse::bad_request(1006, "Gagal mengirim email".to_string()));
            }

            Ok(web::Json(BaseResponse::success(
                200,
                Some("Verifikasi telah terkirim".to_string()),
                "OTP berhasil dikirim".to_string(),
            )))
        }
    }
}