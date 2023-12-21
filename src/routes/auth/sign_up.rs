extern crate bcrypt;

use crate::models::user::UserCredentialResponse;
use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::AppState;
use crate::common::mail;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{SignUpBasicRequest, VerifyOtpSignUpBasicRequest};
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

    let mut sign_up_repository = SignUpRepository::init(&state);

    let user_credential = sign_up_repository.create_user_credential(
        &body.email,
        &body.password,
        &body.full_name,
    ).await.unwrap();

    //begin save otp then send to user
    let user_verification = sign_up_repository
        .create_user_verification(user_credential.clone())
        .await.unwrap();

    let save_otp = sign_up_repository
        .save_otp_sign_up_to_redis(
            user_verification.id.as_str(),
            user_verification.code.as_str(),
            user_verification.user_id.unwrap().as_str(),
        ).await.unwrap();


    let send_email = mail::email::Email::new(
        user_credential.email.clone(),
        user_credential.full_name.clone(),
    );


    let _ = send_email.send_otp_sign_up_basic(
        user_credential.full_name.as_str(),
        user_verification.code.as_str(),
    ).await;


    //send email otp
    Ok(web::Json(BaseResponse::created(
        201,
        Some(user_verification.id),
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
    let session = sign_up_repository
        .get_otp_sign_up_from_redis(body_request.session_id.clone())
        .await
        .unwrap();

    if !session.otp.eq(&body_request.otp) {
        return Err(ErrorResponse::unauthorized("".to_string()));
    }

    let update_status = sign_up_repository
        .update_verification_user_status(session.user_id)
        .await
        .unwrap();


    Ok(web::Json(BaseResponse::success(
        200,
        Some(serde_json::json!({
            "token":session.session_id,
            "credential":UserCredentialResponse::from_credential(update_status)
        })),
        "Berhasil melakukan pendaftaran".to_string())
    ))
}
