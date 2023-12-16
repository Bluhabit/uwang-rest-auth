extern crate bcrypt;

use actix_web::{Responder, Result, web};
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::models::auth::VerifyOtpRequest;

pub async fn sign_in_basic(
    _: web::Data<AppState>,
    body: web::Json<VerifyOtpRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        return Err(ErrorResponse::bad_request(400, "invalid request".to_string()));
    }

    Ok(web::Json(BaseResponse::success(200, Some(""), "".to_string())))
}

pub fn handler(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/auth")
            .route("/sign-in-basic", web::post().to(sign_in_basic))
    );
}
