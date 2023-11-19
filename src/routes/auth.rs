extern crate bcrypt;

use actix_web::{Responder, Result, web};
use bcrypt::{DEFAULT_COST, hash, verify};
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::entity::sea_orm_active_enums::{AuthProvider, Status};
use crate::models::auth::{SignInBasicRequest, SignUpBasicRequest, VerifyOtpRequest};
use crate::repositories::auth::AuthRepository;
use crate::models::auth::SessionModel;

pub async fn sign_in_basic(
    state: web::Data<AppState>,
    body: web::Json<SignInBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {

    //validate request
    let validate_body = body.validate();
    if validate_body.is_err() {
        return Err(ErrorResponse::bad_request(2000, get_readable_validation_message(validate_body.unwrap_err())));
    }

    //find related account
    let mut auth_repo = AuthRepository::init(&state);
    let find_user = auth_repo.get_user_by_email(&body.email).await;
    if find_user.is_none() {
        return Err(ErrorResponse::unauthorized("Cannot find user ".to_string()));
    }

    let user = find_user.clone().unwrap();
    //make sure user is authenticated
    let password_match = verify(&body.password, &user.password).unwrap_or(false);
    if !password_match {
        return Err(ErrorResponse::unauthorized("Username or password invalid ".to_string()));
    }

    //make sure account is using basic auth(email&password)
    if user.auth_provider != AuthProvider::Basic {
        return Err(ErrorResponse::forbidden(1000, "Email used by another account".to_string()));
    }
    if user.status == Status::Suspended {
        return Err(ErrorResponse::forbidden(1002, "Your account suspended".to_string()));
    }
    if user.status == Status::Inactive {
        return Err(ErrorResponse::forbidden(1003, "Your account is inactive".to_string()));
    }

    //set session
    let save_session:Option<SessionModel> = auth_repo.set_user_session(&find_user.unwrap()).await;


    Ok(web::Json(BaseResponse::success(
        200,
        save_session,
        "Sign in success".to_string(),
    )))
}

pub async fn sign_up_basic(
    state: web::Data<AppState>,
    body: web::Json<SignUpBasicRequest>,
) -> Result<impl Responder, ErrorResponse> {
    //validate body
    let validate_body = body.validate();
    if validate_body.is_err() {
        return Err(ErrorResponse::bad_request(400, "Request seems invalid".to_string()));
    }

    let auth_repo = AuthRepository::init(&state);

    //check email already use
    let email_exist = auth_repo.is_email_used(&body.email).await;
    if email_exist {
        return Err(ErrorResponse::bad_request(400, "Email is used by another account".to_string()));
    }

    //save to db
    let hash_password = hash(
        &body.password.to_string(), DEFAULT_COST,
    );
    if hash_password.is_err() {
        return Err(ErrorResponse::bad_request(400, "Cannot".to_string()));
    }

    let result = auth_repo.insert_user_basic(
        &body.email,
        &hash_password.unwrap().to_string(),
        &body.full_name,
    ).await;

    if result.is_err() {
        return Err(ErrorResponse::bad_request(400, "".to_string()));
    }

    let credential = result.unwrap();

    //begin save otp then send to user
    let _ = auth_repo.create_user_verification(
        &credential.clone()
    ).await;

    //send email otp

    Ok(web::Json(BaseResponse::created(201, Some(credential), "OTP Sent please check your email".to_string())))
}

pub async fn verify_otp(
    _: web::Data<AppState>,
    body: web::Json<VerifyOtpRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        return Err(ErrorResponse::bad_request(400, "invalid request".to_string()));
    }
    
    Ok(web::Json(BaseResponse::success(200, Some(""), "".to_string())))
}

pub fn auth_handler(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/auth")
            .route("/sign-in-basic", web::post().to(sign_in_basic))
            .route("/sign-up-basic", web::post().to(sign_up_basic))
            .route("/verify-otp", web::post().to(verify_otp))
    );
}
