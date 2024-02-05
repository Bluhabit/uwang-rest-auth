use std::option::Option::Some;

use actix_web::{web, Responder, Result};
use chrono::Utc;
use serde::Serialize;

use crate::{
    common::{jwt::encode, response},
    AppState,
};

#[derive(Serialize)]
pub struct IndexResponse {
    pub hello: String,
}

pub async fn hello(_state: web::Data<AppState>) -> Result<impl Responder> {
    let current_date = Utc::now().naive_local();

    let enc = encode("Trian".to_string());

    let obj = response::BaseResponse {
        status_code: 200,
        message: enc.unwrap(),
        data: Some(IndexResponse {
            hello: String::from(format!("heh {}", current_date)),
        }),
    };
    Ok(web::Json(obj))
}
