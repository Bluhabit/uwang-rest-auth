use std::future::{ready, Ready};

use actix_web::{FromRequest, HttpRequest};
use actix_web::dev::Payload;

use migration::async_trait::async_trait;

use crate::common::response::ErrorResponse;

pub struct ClientMiddleware {
    pub ip_address: String,
    pub device: String,
}

#[async_trait]
impl FromRequest for ClientMiddleware {
    //this should type to actix::web::Error but the return is string text/plain
    // so that we create custom error see response::ErrorResponse for the implementation
    type Error = ErrorResponse;
    type Future = Ready<Result<Self, Self::Error>>;
    fn from_request(req: &HttpRequest, _: &mut Payload) -> Self::Future {
        let ip = req.peer_addr();
        let device = req.headers().get("X-Agent").unwrap().to_str();

        if ip.is_none() {
            return ready(Err(ErrorResponse::bad_request(401, "Alamat IP tidak dikenali".to_string())));
        }

        if device.is_err() {
            return ready(Err(ErrorResponse::bad_request(401, "Device tidak dikenali".to_string())));
        }

        ready(Ok(ClientMiddleware {
            ip_address: ip.unwrap().ip().to_string(),
            device: device.unwrap().to_string(),
        }))
    }
}
