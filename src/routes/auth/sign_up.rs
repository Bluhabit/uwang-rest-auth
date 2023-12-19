extern crate bcrypt;

use actix_web::{web, Responder, Result};
use bcrypt::{hash, DEFAULT_COST};
use validator::Validate;

use crate::common::mail;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::models::auth::SignUpBasicRequest;
use crate::models::auth::VerifiedOtpSignUpBasicRequest;
use crate::models::user::UserCredentialResponse;
use crate::repositories::auth::sign_up::SignUpRepository;
use crate::AppState;

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

    let mut auth_repo = SignUpRepository::init(&state);
    //check email already use
    let email_exist = auth_repo.is_email_used(&body.email).await;
    if email_exist {
        return Err(ErrorResponse::bad_request(
            400,
            "Email tidak dapat digunakan".to_string(),
        ));
    }

    //save to db
    let hash_password = hash(&body.password.to_string(), DEFAULT_COST);
    if hash_password.is_err() {
        return Err(ErrorResponse::bad_request(
            1000,
            "Gagal membuat akun ".to_string(),
        ));
    }

    let result = auth_repo
        .insert_user_basic(
            &body.email,
            &hash_password.unwrap().to_string(),
            &body.full_name,
        )
        .await;

    if result.is_err() {
        return Err(ErrorResponse::bad_request(
            1001,
            "Gagal membuat akun".to_string(),
        ));
    }

    let credential = result.unwrap();

    //begin save otp then send to user
    let user_verification = auth_repo
        .create_user_verification(&credential.clone())
        .await;

    if user_verification.is_err() {
        return Err(ErrorResponse::bad_request(
            1002,
            "Gagal membuat verifikasi akun".to_string(),
        ));
    }

    //save otp to redis
    let user = user_verification.unwrap();
    let saved_otp = auth_repo
        .save_otp_sign_up_to_redis(user.id.as_str(), user.code.as_str())
        .await;

    if saved_otp.is_err() {
        return Err(ErrorResponse::bad_request(1002, saved_otp.unwrap_err()));
    }

    //send email to email
    let data = credential.clone();
    let send_email = mail::email::Email::new(data.full_name, data.email);

    let _ = send_email
        .send_verification_code(&credential.email.as_str(), user.code.as_str())
        .await;

    //send email otp
    Ok(web::Json(BaseResponse::created(
        201,
        Some(UserCredentialResponse::from_credential(credential)),
        "Registrasi berhasil, silahkan ceh email Anda".to_string(),
    )))
}

pub fn handler(cfg: &mut web::ServiceConfig) {
    cfg.service(web::scope("/api/auth").route("/sign-up-basic", web::post().to(sign_up_basic)));
}

pub async fn verified_otp_sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<VerifiedOtpSignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let otp = body.otp;
    if otp.is_err(){
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }
    // disini gw mencoba untuk membuat variable yang berguna untuk memanggil otp dari VerifiedOtpSignUpBasicRequest tapi kenapa error ya bang padahal kan kemaren udh gw bikin, modul ya namanya kalau ngga salah

    // kalau diaa berhasil masuk ke codingan terakhir
    // kalau gagal : 1. OTP tidak sesuai, 2. OTP sudah expired,

    //send email otp
    Ok(web::Json(BaseResponse::success(
        200, Some(“”)
        , "".to_string())
    ));
}
