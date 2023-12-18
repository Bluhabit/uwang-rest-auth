extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::{AppState, common};
use crate::common::jwt::decode;
use crate::common::mail::email;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::models::auth::{SignInBasicRequest, SignInGoogleRequest};
use crate::repositories::auth::sign_in::SignInRepository;

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
        .get_user_credential_by_email(&body.email).await;


    if find_user.is_err() {
        return Err(ErrorResponse::bad_request(1001, "Akun tidak ditemukan".to_string()));
    }

    let user_credential = find_user.unwrap();
    if user_credential.auth_provider != AuthProvider::Basic {
        return Err(ErrorResponse::bad_request(
            1003,
            "Email sudah digunakan oleh akun lain".to_string(),
        ));
    }

    match user_credential.status {
        UserStatus::Inactive => Err(
            ErrorResponse::bad_request(
                1004,
                "Akun Anda sudah tidak aktif".to_string(),
            )
        ),
        UserStatus::Suspended => Err(
            ErrorResponse::bad_request(
                1005,
                "Akun Anda tersuspend".to_string(),
            )
        ),
        UserStatus::WaitingConfirmation => Err(
            ErrorResponse::bad_request(
                1006,
                "Akun Anda belum terverifikasi, silahkan cek Email untuk memverifikasi akun.".to_string(),
            )
        ),
        UserStatus::Active => {
            if bcrypt::verify(&body.password, &user_credential.password).is_err() {
                return Err(
                    ErrorResponse::bad_request(
                        1007,
                        "Email atau password salah atau tidak sesuai".to_string(),
                    )
                );
            }

            //save otp
            let user_verification = sign_in_repository
                .create_user_verification(&user_credential.clone())
                .await;
            if user_verification.is_err() {
                return Err(
                    ErrorResponse::bad_request(
                        1008,
                        "Gagal membuat verifikasi kode, silahkan coba beberapa saat lagi".to_string(),
                    )
                );
            }
            let verification_data = user_verification.unwrap();

            let save_to_redis = sign_in_repository
                .save_otp_sign_in_to_redis(
                    &verification_data.id,
                    &verification_data.code,
                ).await;
            if save_to_redis.is_err() {
                return Err(
                    ErrorResponse::bad_request(
                        1009,
                        "Gagal membuat verifikasi kode, silahkan coba beberapa saat lagi".to_string(),
                    )
                );
            }

            let email = email::Email::new(user_credential.email, user_credential.full_name.clone());
            let _ = email.send_otp_sign_in_basic(
                &user_credential.full_name,
                &verification_data.code,
            ).await;

            Ok(web::Json(BaseResponse::success(
                200,
                Some(verification_data.id),
                "Masuk berhasil ".to_string(),
            )))
        }
    }
}

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

    let mut sign_in_repository = SignInRepository::init(
        &state
    );

    let find_user = sign_in_repository.get_user_credential_by_email(
        &credential.unwrap().claims.email
    ).await;

    if find_user.is_err() {
        return Err(
            ErrorResponse::bad_request(
                1002,
                "Akun belum terdaftar".to_string(),
            )
        );
    }

    let user_credential = find_user.unwrap();

    if user_credential.auth_provider != AuthProvider::Google {
        return Err(
            ErrorResponse::bad_request(
                1003,
                "Akun sudah digunakan".to_string(),
            )
        );
    }

    match user_credential.status {
        UserStatus::Inactive => Err(
            ErrorResponse::bad_request(
                1004,
                "Akun tidak aktif".to_string(),
            )
        ),
        UserStatus::Suspended => Err(
            ErrorResponse::bad_request(
                1005,
                "Akun anda tersuspend".to_string(),
            )
        ),
        UserStatus::WaitingConfirmation => Err(
            ErrorResponse::bad_request(
                1006,
                "Akun belum terverifikasi".to_string(),
            )
        ),
        UserStatus::Active => {
            let saved_session = sign_in_repository
                .save_user_session_to_redis(&user_credential).await;
            if saved_session.is_err(){
                return Err(ErrorResponse::unauthorized(saved_session.unwrap_err()));
            }

            Ok(web::Json(BaseResponse::success(
                200, Some(
                    serde_json::json!({
                        token:saved_session.unwrap(),
                        credential:user_credential
                    })
                ),
                "Login berhasil".to_string())
            ))
        }
    }
}
