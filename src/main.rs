use std::string::ToString;
use std::sync::Arc;

use actix_cors::Cors;
use actix_web::{App, http, HttpResponse, HttpServer, middleware, web};
use actix_web::error::InternalError;
use actix_web::http::StatusCode;
use actix_web::web::Data;
use dotenv::dotenv;
use handlebars::Handlebars;
use redis::Client;
use sea_orm::{Database, DatabaseConnection};
use serde_json::json;
use crate::common::mail::email::Email;

use crate::common::response::ErrorResponse;
use crate::common::sse::sse_emitter::SseBroadcaster;
use crate::routes::auth::forgot_password::{forgot_password, set_new_password, verify_otp_forgot_password};
use crate::routes::auth::sign_in::{sign_in_basic, sign_in_google, verify_otp_sign_in_basic};
use crate::routes::auth::sign_up::{sign_up_basic, verify_otp_sign_up_basic};
use crate::routes::index::hello;
use crate::routes::user::user::complete_profile;

mod common;

// mod entity;
mod entity;
mod models;
mod repositories;
mod request_filter;
mod routes;

#[derive(Debug, Clone)]
pub struct AppState {
    db: DatabaseConnection,
    cache: Client,
    sse_emitter: Arc<SseBroadcaster>,
}

const DB_URL_KEY: &str = "DATABASE_URL";
const REDIS_URL_KEY: &str = "REDIS_URL";
const DB_URL_DEFAULT_VALUE: &str = "postgres://user:password@host:port/db";
const REDIS_URL_DEFAULT_VALUE: &str = "redis://user:password@host:port";

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    dotenv().ok();
    // access logs are printed with the INFO level so ensure it is enabled by default
    env_logger::init_from_env(env_logger::Env::new().default_filter_or("info"));
    std::env::set_var("RUST_BACKTRACE", "full".to_string());
    if std::env::var("RUST_LOG").is_err() {
        std::env::set_var("RUST_LOG", "actix_web=info")
    }
    let postgres_url: String = std::env::var(DB_URL_KEY)
        .unwrap_or(DB_URL_DEFAULT_VALUE.to_string());

    let redis_url: String = std::env::var(REDIS_URL_KEY)
        .unwrap_or(REDIS_URL_DEFAULT_VALUE.to_string());

    let db: DatabaseConnection = Database::connect(postgres_url.clone())
        .await
        .expect(format!("failed to connect postgres {}", postgres_url).as_str());

    let cache: Client = Client::open(redis_url.clone())
        .expect(format!("Invalid connection Url {}", redis_url).as_str());

    let sse_emitter = SseBroadcaster::create();


    let state: AppState = AppState {
        db: db.clone(),
        cache: cache.clone(),
        sse_emitter: sse_emitter.clone(),
    };


    HttpServer::new(move || {
        let cors = Cors::default()
            .allowed_origin_fn(|origin, _req_head| {
                let origin = origin.as_bytes();
                let _ = origin.ends_with(b".bluhabit.id");

                true
            })
            .allowed_methods(vec!["GET", "POST", "PUT", "PATCH", "DELETE"])
            .allowed_headers(vec![http::header::AUTHORIZATION, http::header::ACCEPT])
            .allowed_header(http::header::CONTENT_TYPE);

        App::new()
            .wrap(cors)
            .wrap(middleware::Logger::default())
            .wrap(middleware::Logger::new(
                "%a %r %s %b %{Referer}i %{User-Agent}i %T",
            ))
            .app_data(Data::new(state.clone()))
            .app_data(web::JsonConfig::default().error_handler(|err, _| {
                InternalError::from_response(
                    format!("cause {}", err.to_string()),
                    HttpResponse::build(StatusCode::BAD_REQUEST)
                        .json(
                            ErrorResponse::bad_request(
                                1000,
                                err.to_string(),
                            )
                        ),
                )
                    .into()
            }))
            .configure(init)
    })
        .bind(("0.0.0.0", 8000))?
        .run()
        .await
}

pub fn init(cfg: &mut web::ServiceConfig) {
    cfg.route("/", web::get().to(hello));
    cfg.route("/email", web::get().to(index));
    cfg.service(
        web::scope("/api/auth")
            .route("/sign-up-basic", web::post().to(sign_up_basic))
            .route("/sign-up-basic/verify-otp", web::post().to(verify_otp_sign_up_basic))
            .route("/sign-in-basic", web::post().to(sign_in_basic))
            .route("/sign-in-basic/verify-otp", web::post().to(verify_otp_sign_in_basic))
            .route("/sign-in-google", web::post().to(sign_in_google))
            .route("/forgot-password", web::post().to(forgot_password))
            .route("/forgot-password/verify-otp", web::post().to(verify_otp_forgot_password))
            .route("/forgot-password/set-password", web::post().to(set_new_password))
    );
    cfg.service(
        web::scope("/api/user")
            .route("/complete-profile", web::post().to(complete_profile))
    );

    routes::event_stream::event_stream_handler(cfg)
}

pub async fn index(
    _: Data<AppState>
) -> HttpResponse {

    let mail = Email::new("triandamai@gmail.com".to_string(),"Trian".to_string())
        .send_otp_sign_in_basic("Trian","1234")
        .await
        .unwrap();
    HttpResponse::Ok().body(mail)
}