use actix_web::{Responder, web};
use actix_web::web::Json;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::request_filter::jwt_middleware::JwtMiddleware;

pub async fn get_users(
    _: web::Data<AppState>,
    _: JwtMiddleware,
) -> Result<impl Responder, ErrorResponse> {
    Ok(Json(BaseResponse::success(200, Some("sas"), "Success".to_string())))
}

pub async fn update_basic_profile(
    state: web::Data<AppState>,
    jwt: JwtMiddleware,
) -> Result<impl Responder, ErrorResponse> {

    Ok(Json(BaseResponse::success(200, Some("sas"), "Success".to_string())))
}