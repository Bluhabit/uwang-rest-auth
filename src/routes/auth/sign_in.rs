extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::{AppState, common};
use crate::common::mail::email;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::{SignInBasicRequest, SignInGoogleRequest, VerifyOtpSignInBasicRequest};
use crate::repositories::auth::sign_in::SignInRepository;
use crate::models::user::UserCredentialResponse;

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
        .get_user_by_email(&body.password, &body.email)
        .await;
    if find_user.is_err() {
        return Err(find_user.unwrap_err());
    }
    let user = find_user.unwrap();

    //save otp to db
    let user_verification = sign_in_repository
        .create_user_verification(user.clone())
        .await;

    if user_verification.is_err() {
        return Err(user_verification.unwrap_err());
    }
    let verification = user_verification
        .unwrap();


    //save to redis
    let user_session = sign_in_repository.save_otp_sign_in_to_redis(
        &verification.id,
        &verification.code,
        &verification.user_id.unwrap(),
    ).await;

    if user_session.is_err() {
        return Err(user_session.unwrap_err());
    }
    let session = user_session.unwrap();


    //sending email
    let email = email::Email::new(
        user.email,
        user.full_name.clone(),
    );

    let _ = email.send_otp_sign_in_basic(
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
    let redis_otp = sign_in_repository
        .get_otp_sign_in_from_redis(&body.session_id)
        .await;

    if redis_otp.is_err() {
        return Err(redis_otp.unwrap_err());
    }
    let data_otp = redis_otp.unwrap();


    //match otp should be valid
    if !data_otp.otp.eq(&body.otp) {
        return Err(ErrorResponse::bad_request(400, "Otp tidak sesuai atau kadaluarsa [3]".to_string()));
    }

    let user_credential = sign_in_repository
        .get_user_credential(data_otp.user_id)
        .await;

    if user_credential.is_err() {
        return Err(user_credential.unwrap_err());
    }
    let user = user_credential.unwrap();

    let save_session = sign_in_repository
        .save_user_session_to_redis(&user)
        .await;

    if save_session.is_err() {
        return Err(save_session.unwrap_err());
    }
    let saved_session = save_session.unwrap();

    Ok(web::Json(BaseResponse::success(
        200,
        Some(serde_json::json!({
            "token":saved_session.token,
            "user_credential":user
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
    let google_credential = common::jwt::decode_google_token(body.token.clone());
    if google_credential.is_err() {
        return Err(ErrorResponse::bad_request(
            1001,
            google_credential.err().unwrap().to_string(),
        ));
    }

    let sign_in_repository = SignInRepository::init(&state);
    //get data and validate account
    let get_user_by_email = sign_in_repository
        .get_user_by_google(google_credential.unwrap().claims, )
        .await;

    if get_user_by_email.is_err() {
        return Err(get_user_by_email.unwrap_err());
    }
    let user = get_user_by_email.unwrap();
    //save to redis
    let saved_session = sign_in_repository
        .save_user_session_to_redis(&user).await;

    if saved_session.is_err() {
        return Err(saved_session.unwrap_err());
    }


    //success
    Ok(web::Json(BaseResponse::success(
        200,
        Some(serde_json::json!({
            "token":saved_session.unwrap().token,
            "credential":UserCredentialResponse::from_credential(user)
        })),
        "Login berhasil silahkan melanjutkan".to_string())
    ))
}
