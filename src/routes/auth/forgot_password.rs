use actix_web::{Responder, web};
use validator::Validate;
use crate::AppState;
use crate::common::mail::email;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{ForgotPasswordRequest, SetForgotPasswordRequest, VerifyOtpForgotPasswordRequest};
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
    let find_user_by_email = forgot_password_repository
        .get_user_by_email(body.email.clone())
        .await;

    if find_user_by_email.is_err() {
        return Err(find_user_by_email.unwrap_err());
    }
    let user = find_user_by_email.unwrap();

    let user_verification =
        forgot_password_repository
            .create_user_verification(user.clone())
            .await;

    if user_verification.is_err() {
        return Err(user_verification.unwrap_err());
    }
    let verification = user_verification.unwrap();

    let redis_otp = forgot_password_repository
        .save_otp_forgot_password_to_redis(
            verification.id.as_str(),
            verification.code.as_str(),
            user.id.as_str(),
        )
        .await;
    if redis_otp.is_err() {
        return Err(redis_otp.unwrap_err());
    }
    let session = redis_otp.unwrap();
    //sending email
    let email = email::Email::new(
        user.email,
        user.full_name.clone(),
    );

    let _ = email.send_otp_forgot_password_basic(
        &user.full_name,
        &verification.code,
    ).await;
    //all process success
    Ok(web::Json(BaseResponse::success(
        200,
        Some(session.session_id),
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
    let redis_otp = forgot_password_repository
        .get_otp_forgot_password_from_redis(body.session_id.clone())
        .await;
    if redis_otp.is_err() {
        return Err(redis_otp.unwrap_err());
    }
    let data_otp = redis_otp.unwrap();
    //match otp should be valid
    if !data_otp.otp.eq(&body.otp) {
        return Err(ErrorResponse::bad_request(400, "Otp tidak sesuai atau kadaluarsa [3]".to_string()));
    }

    let user_credential = forgot_password_repository
        .get_user_credential(data_otp.user_id)
        .await;
    if user_credential.is_err() {
        return Err(user_credential.unwrap_err());
    }
    let user = user_credential.unwrap();

    let session_key = forgot_password_repository
        .save_session_forgot_password(user)
        .await;

    if session_key.is_err() {
        return Err(session_key.unwrap_err());
    }
    let session = session_key.unwrap();
    Ok(web::Json(BaseResponse::success(
        200,
        Some(session),
        "Berhasil, silahkan ubah password Anda".to_string(),
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

    let forgot_password_repository = ForgotPasswordRepository::init(&state);

    let get_user_from_session = forgot_password_repository
        .get_user_by_session(body.session_id.clone())
        .await;
    if get_user_from_session.is_err() {
        return Err(get_user_from_session.unwrap_err());
    }
    let user = get_user_from_session.unwrap();
    let name = forgot_password_repository
        .set_new_password(user, &body.password)
        .await;
    if name.is_err() {
        return Err(name.unwrap_err());
    }

    Ok(web::Json(BaseResponse::success(
        201,
        Some(name.unwrap()),
        "Berhasil merubah password".to_string(),
    )))
}