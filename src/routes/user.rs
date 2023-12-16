use actix_web::{Responder, Result, web};
use actix_web::web::Json;

use crate::{AppState, common::response::BaseResponse, request_filter};
use crate::common::response::ErrorResponse;

pub async fn get_users(
    _: web::Data<AppState>,
    _: request_filter::jwt_middleware::JwtMiddleware,
) -> Result<impl Responder, ErrorResponse> {

    Ok(Json(BaseResponse::success(200, Some("sas"), "Success".to_string())))
}

pub fn user_handler(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/user")
            .route("/get", web::get().to(get_users))
    );
}
