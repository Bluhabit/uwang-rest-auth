use crate::{common::response, AppState};
use actix_web::{web, Responder, Result};
use serde::Serialize;
use std::option::Option::Some;
use chrono::{FixedOffset, Utc};

#[derive(Serialize)]
pub struct IndexResponse {
    pub hello: String,
}

pub async fn hello(_state: web::Data<AppState>) -> Result<impl Responder> {

    let current_date = Utc::now().naive_local();
    let obj = response::BaseResponse {
        status_code: 200,
        message: String::from("Hehe"),
        data: Some(IndexResponse {
            hello: String::from(format!("heh {}",current_date)),
        }),
    };
    Ok(web::Json(obj))
}