use actix_web::{HttpRequest, Responder, web};
use actix_web::Result as WebResult;
use actix_web::web::Json;
use validator::Validate;
use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::models::event::SendEventRequest;

async fn register_event(
    state: web::Data<AppState>,
    req: HttpRequest,
) -> impl Responder {
    let header = req
        .headers()
        .get("context");
    if header.is_none() {
        return state.sse_emitter.reject_client().await;
    }
    let id =
        header.unwrap().to_str().unwrap();

    return state.sse_emitter.new_client(id).await;
}

async fn send(
    state: web::Data<AppState>,
    body: web::Json<SendEventRequest>,
) -> WebResult<impl Responder, ErrorResponse> {
    let validate = body.validate();
    if validate.is_err() {
        return Err(ErrorResponse::bad_request(
            400,
            validate.unwrap_err().to_string(),
        ));
    }
    state.sse_emitter
        .send_to(
            body.to.as_str(),
            body.event_name.as_str(),
            body.message.as_str(),
        ).await;

    return Ok(Json(BaseResponse::success(
        200,
        Some(body),
        "".to_string(),
    )));
}

async fn broadcast(
    state: web::Data<AppState>,
    body: web::Json<SendEventRequest>,
) -> WebResult<impl Responder, ErrorResponse> {
    let validate = body.validate();
    if validate.is_err() {
        return Err(ErrorResponse::bad_request(
            400,
            validate.unwrap_err().to_string(),
        ));
    }
    state.sse_emitter.broadcast(body.event_name.as_str(), body.message.as_str()).await;
    return Ok(Json(BaseResponse::success(
        200,
        Some(body),
        "".to_string(),
    )));
}

pub fn event_stream_handler(cfg: &mut web::ServiceConfig) {
    cfg
        .service(
            web::scope("/event")
                .route("/subscribe", web::get().to(register_event))
                .route("/send", web::post().to(send))
                .route("/broadcast", web::post().to(broadcast))
        );
}