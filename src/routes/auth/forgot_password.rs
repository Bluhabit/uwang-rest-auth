use actix_web::{Responder, web};
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{ForgotPasswordRequest, ResendOtpForgotPasswordRequest, SetForgotPasswordRequest, VerifyOtpForgotPasswordRequest};
use crate::repositories::auth::forgot_password::ForgotPasswordRepository;

pub async fn forgot_password(
    state: web::Data<AppState>,
    body: web::Json<ForgotPasswordRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }
    let mut forgot_password_repository = ForgotPasswordRepository::init(&state);
    let send_otp_forgot_password = forgot_password_repository
        .forgot_password(&body.email.clone())
        .await;

    if send_otp_forgot_password.is_err() {
        return Err(send_otp_forgot_password.unwrap_err());
    }
    let session_id = send_otp_forgot_password.unwrap();


    //all process success
    Ok(web::Json(BaseResponse::success(
        200,
        Some(session_id),
        "Kode OTP sudah dikirim ke email Anda ".to_string(),
    )))
}

pub async fn verify_otp_forgot_password(
    state: web::Data<AppState>,
    body: web::Json<VerifyOtpForgotPasswordRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let validate_body = body.validate();
        if validate_body.is_err() {
            let message = get_readable_validation_message(validate_body.err());
            return Err(ErrorResponse::bad_request(400, message));
        }
    }

    let mut forgot_password_repository = ForgotPasswordRepository::init(&state);
    let verify_otp = forgot_password_repository
        .verify_otp_forgot_password(&body.session_id, &body.otp)
        .await;
    if verify_otp.is_err() {
        return Err(verify_otp.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        Some(verify_otp.unwrap()),
        "Berhasil, silahkan ubah password Anda".to_string(),
    )))
}

pub async fn resend_otp_forgot_password(
    state: web::Data<AppState>,
    body: web::Json<ResendOtpForgotPasswordRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let validate_body = body.validate();
        if validate_body.is_err() {
            let message = get_readable_validation_message(validate_body.err());
            return Err(ErrorResponse::bad_request(400, message));
        }
    }

    let forgot_password_repository = ForgotPasswordRepository::init(&state);
    let verify_otp = forgot_password_repository
        .resend_otp_forgot_password(&body.session_id)
        .await;
    if verify_otp.is_err() {
        return Err(verify_otp.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        Some(verify_otp.unwrap()),
        "Otp sudah dikirim ke email kamu.".to_string(),
    )))
}

pub async fn set_new_password(
    state: web::Data<AppState>,
    body: web::Json<SetForgotPasswordRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let mut forgot_password_repository = ForgotPasswordRepository::init(&state);
    let set_password = forgot_password_repository
        .set_new_password(
            &body.session_id,
            &body.password,
        ).await;
    if set_password.is_err() {
        return Err(set_password.unwrap_err());
    }
    Ok(web::Json(BaseResponse::success(
        201,
        Some(set_password.unwrap()),
        "Berhasil merubah password".to_string(),
    )))
}