use std::fmt::Debug;

use actix_web::{HttpResponse, ResponseError};
use actix_web::http::StatusCode;
use serde::{Deserialize, Serialize};

use crate::common::utils::get_readable_validation_message;

#[derive(Debug,Serialize,Deserialize)]
pub enum CustomErrorType {
    SeaOrmError,
    ValidationError,
    Serialize
}

#[derive(Debug,Serialize,Deserialize)]
pub struct CustomError {
    pub message: Option<String>,
    pub err_type: CustomErrorType,
}

impl std::fmt::Display for CustomError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{:?}", self)
    }
}

impl From<validator::ValidationErrors> for CustomError {
    fn from(err: validator::ValidationErrors) -> CustomError {
        CustomError {
            message: Some(get_readable_validation_message(err)),
            err_type: CustomErrorType::ValidationError,
        }
    }
}

impl ResponseError for CustomError {
    fn status_code(&self) -> StatusCode {
        match self.err_type {
            CustomErrorType::SeaOrmError => StatusCode::INTERNAL_SERVER_ERROR,
            CustomErrorType::ValidationError => StatusCode::BAD_REQUEST,
            CustomErrorType::Serialize => StatusCode::BAD_REQUEST
        }
    }

    fn error_response(&self) -> HttpResponse {
        HttpResponse::build(self.status_code()).json(self)
    }
}