extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{ResendOtpSignInBasicRequest, SignInBasicRequest, SignInGoogleRequest, VerifyOtpSignInBasicRequest};
use crate::repositories::auth::sign_in::SignInRepository;
use crate::request_filter::client_middleware::ClientMiddleware;

/// == region sign in basic ==
pub async fn sign_in_basic(
    state: web::Data<AppState>,
    client: ClientMiddleware,
    body: web::Json<SignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }
    let mut sign_in_repository = SignInRepository::init(&state);

    let sign_in = sign_in_repository
        .sign_in_by_email(&body.email, &body.password, &client)
        .await;

    if sign_in.is_err() { return Err(sign_in.unwrap_err()); }

    Ok(web::Json(BaseResponse::success(
        200,
        Some(sign_in.unwrap()),
        "Kode OTP sudah dikirim ke email Anda ".to_string(),
    )))
}

/// == end region ==
/// == region verify otp ==
pub async fn verify_otp_sign_in_basic(
    state: web::Data<AppState>,
    client: ClientMiddleware,
    body: web::Json<VerifyOtpSignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let mut sign_in_repository = SignInRepository::init(&state);
    let verify_otp = sign_in_repository
        .verify_otp_sign_in(
            &body.session_id,
            &body.otp,
            &client,
        ).await;

    if verify_otp.is_err() {
        return Err(verify_otp.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        verify_otp.unwrap(),
        "Verifikasi otp berhasil".to_string(),
    )))
}

/// == end region verify otp ==
pub async fn resend_otp_sign_in_basic(
    state: web::Data<AppState>,
    client: ClientMiddleware,
    body: web::Json<ResendOtpSignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let sign_in_repository = SignInRepository::init(&state);
    let resend_otp = sign_in_repository
        .resend_otp_sign_in_basic(body.session_id.as_str(), &client)
        .await;

    if resend_otp.is_err() {
        return Err(resend_otp.unwrap_err());
    }
    Ok(web::Json(BaseResponse::success(
        200,
        resend_otp.unwrap(),
        "Otp telah dikirim ke email kamu.".to_string(),
    )))
}

/// == region sign in google ==
pub async fn sign_in_google(
    state: web::Data<AppState>,
    client: ClientMiddleware,
    body: web::Json<SignInGoogleRequest>,
) -> Result<impl Responder, ErrorResponse> {
    //validate incoming request
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(
            1000,
            message,
        ));
    }

    let sign_in_repository = SignInRepository::init(&state);
    let get_user_by_google = sign_in_repository
        .sign_in_google(&body, &client)
        .await;

    if get_user_by_google.is_err() {
        return Err(get_user_by_google.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        200,
        get_user_by_google.unwrap(),
        "Login berhasil silahkan melanjutkan".to_string())
    ))
}
