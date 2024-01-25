extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{CompleteProfileSignUpBasicRequest, ResendOtpSignUpBasicRequest, SignUpBasicRequest, VerifyOtpSignUpBasicRequest};
use crate::repositories::auth::sign_up::SignUpRepository;

pub async fn sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<SignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let mut sign_up_repository = SignUpRepository::init(&state);

    let sign_up = sign_up_repository
        .sign_up_by_email(&body.email)
        .await;

    if sign_up.is_err() {
        return Err(sign_up.unwrap_err());
    }
    let sign_up = sign_up.unwrap();

    Ok(web::Json(BaseResponse::created(
        201,
        Some(sign_up),
        "Registrasi berhasil, silahkan ceh email Anda untuk memverifikasi akun.".to_string(),
    )))
}

pub async fn verify_otp_sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<VerifyOtpSignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let sign_up_repository = SignUpRepository::init(&state);
    let redis_otp = sign_up_repository
        .verify_otp_sign_up(&body.session_id, &body.otp)
        .await;
    if redis_otp.is_err() {
        return Err(redis_otp.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        Some(redis_otp.unwrap()),
        "Berhasil melakukan pendaftaran".to_string())
    ))
}

pub async fn resend_otp_sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<ResendOtpSignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let sign_up_repository = SignUpRepository::init(&state);

    let resend_otp = sign_up_repository.resend_otp_sign_up_basic(
        &body.session_id
    ).await;

    if resend_otp.is_err() {
        return Err(resend_otp.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        Some(resend_otp.unwrap()),
        "Berhasil melakukan pendaftaran".to_string())
    ))
}

pub async fn complete_profile_sign_up(
    state: web::Data<AppState>,
    body: web::Json<CompleteProfileSignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let mut sign_up_repository = SignUpRepository::init(&state);
    let complete_profile = sign_up_repository
        .complete_profile(&body.session_id, &body.full_name, &body.date_of_birth)
        .await;

    if complete_profile.is_err(){
        return Err(complete_profile.unwrap_err());
    }
    Ok(web::Json(BaseResponse::success(
        200,
        Some(body),
        "Berhasil melakukan pendaftaran".to_string())
    ))
}