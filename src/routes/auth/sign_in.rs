extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::{AppState, common};
use crate::common::mail::email;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::entity::sea_orm_active_enums::AuthProvider;
use crate::models::auth::{SignInBasicRequest, SignInGoogleRequest, VerifyOtpSignUpBasicRequest, VerifyOtpSignInBasicRequest};
use crate::repositories::auth::sign_in::SignInRepository;

//region sign in basic
pub async fn sign_in_basic(
    state: web::Data<AppState>,
    body: web::Json<SignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    //validate incoming request
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }
    let mut sign_in_repository = SignInRepository::init(&state);
    //get user by email
    let find_user = sign_in_repository
        .get_user_by_email_sign_in(&body.password, &body.email, AuthProvider::Basic)
        .await
        .unwrap();

    //save otp to db
    let user_verification = sign_in_repository
        .create_user_verification(find_user.clone())
        .await.unwrap();

    //save to redis
    let user_session = sign_in_repository.save_otp_sign_in_to_redis(
        &user_verification.id,
        &user_verification.code,
        &user_verification.user_id.unwrap(),
    ).await.unwrap();


    //sending email
    let email = email::Email::new(
        find_user.email,
        find_user.full_name.clone(),
    );

    let _ = email.send_otp_sign_in_basic(
        &find_user.full_name,
        &user_verification.code,
    ).await;

    //all process success
    Ok(web::Json(BaseResponse::success(
        200,
        Some(user_session.session_id),
        "Kode OTP sudah dikirim ke email Anda ".to_string(),
    )))
}

//end region
//region verify otp
pub async fn verify_otp_sign_in_basic(
    state: web::Data<AppState>,
    body: web::Json<VerifyOtpSignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    //validate incoming request
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    //get session from redis
    let sign_in_repository = SignInRepository::init(&state);
    let session = sign_in_repository
        .get_otp_sign_in_from_redis(&body.session_id)
        .await.unwrap();

    //match otp should be valid
    if !session.otp.eq(&body.otp) {
        return Err(ErrorResponse::bad_request(400, "".to_string()));
    }

    let user_credential = sign_in_repository
        .get_user_credential(session.user_id)
        .await.unwrap();

    let save_session = sign_in_repository
        .save_user_session_to_redis(&user_credential)
        .await.unwrap();


    Ok(web::Json(BaseResponse::success(
        200,
        Some(serde_json::json!({
            "token":save_session.token,
            "user_credential":user_credential
        })),
        "Verifikasi otp berhasil".to_string(),
    )))
}

//end region
pub async fn sign_in_google(
    state: web::Data<AppState>,
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

    //extract token
    let credential = common::jwt::decode_google_token(body.token.clone());
    if credential.is_err() {
        return Err(ErrorResponse::bad_request(
            1001,
            credential.err().unwrap().to_string(),
        ));
    }

    let mut sign_in_repository = SignInRepository::init(&state);
    //get data and validate account
    let find_user = sign_in_repository
        .get_user_by_email_sign_in(
            "",
            &credential.unwrap().claims.email,
            AuthProvider::Google,
        )
        .await;

    //save to redis
    let saved_session = sign_in_repository
        .save_user_session_to_redis(&find_user.unwrap()).await;


    //success
    Ok(web::Json(BaseResponse::success(
        200,
        Some(saved_session.unwrap()),
        "Login berhasil otp sudah dikirimkan ke email Anda".to_string())
    ))
}
