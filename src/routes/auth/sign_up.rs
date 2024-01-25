extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::AppState;
use crate::common::mail;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{SignUpBasicRequest, VerifyOtpSignUpBasicRequest};
use crate::models::user::UserCredentialResponse;
use crate::repositories::auth::sign_up::SignUpRepository;

pub async fn sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<SignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    //validate body
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let sign_up_repository = SignUpRepository::init(&state);

    let user_credential = sign_up_repository.create_user_credential(
        &body.email,
        &body.password
    ).await;

    if user_credential.is_err() {
        return Err(user_credential.unwrap_err());
    }

    let user = user_credential.unwrap();

    //begin save otp then send to user

    let send_email = mail::email::Email::new(
        user.email.clone(),
        user.full_name.clone(),
    );

    let sended = send_email.send_otp_sign_up_basic(
       serde_json::json!({

       })
    ).await;

    if sended.is_err(){
        return Err(ErrorResponse::bad_request(400,sended.unwrap_err().to_string()))
    }


    //send email otp
    Ok(web::Json(BaseResponse::created(
        201,
        Some(""),
        "Registrasi berhasil, silahkan ceh email Anda".to_string(),
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

    let mut sign_up_repository = SignUpRepository::init(&state);
    let body_request = body;
    let redis_otp = sign_up_repository
        .get_otp_sign_up_from_redis(body_request.session_id.clone())
        .await;

    if redis_otp.is_err() {
        return Err(redis_otp.unwrap_err());
    }
    let otp_data = redis_otp.unwrap();

    if !otp_data.otp.eq(&body_request.otp) {
        return Err(ErrorResponse::unauthorized("".to_string()));
    }

    let updated_status = sign_up_repository
        .update_verification_user_status(otp_data.user_id)
        .await;

    if updated_status.is_err() {
        return Err(updated_status.unwrap_err());
    }

    let user_verification = updated_status.unwrap();

    let saved_session = sign_up_repository
        .save_user_session_to_redis(&user_verification.clone())
        .await;

    if saved_session.is_err() {
        return Err(saved_session.unwrap_err());
    }
    let session = saved_session.unwrap();

    let data = UserCredentialResponse::from_credential(user_verification);
    Ok(web::Json(BaseResponse::success(
        200,
        Some(serde_json::json!({
            "token":session.token,
            "credential":data
        })),
        "Berhasil melakukan pendaftaran".to_string())
    ))
}