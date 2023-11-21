use crate::{common::response, AppState};
use actix_web::{web, Responder, Result};
use serde::Serialize;
use std::option::Option::Some;

#[derive(Serialize)]
pub struct IndexResponse {
    pub hello: String,
}

pub async fn hello(_state: web::Data<AppState>) -> Result<impl Responder> {
    let obj = response::BaseResponse {
        status_code: 200,
        message: String::from("Hehe"),
        data: Some(IndexResponse {
            hello: String::from("Trian"),
        }),
    };
    Ok(web::Json(obj))
}

pub fn index_handler(cfg: &mut web::ServiceConfig) {
    cfg.route("/", web::get().to(hello));
}
