use std::string::ToString;
use actix_web::error::InternalError;
use actix_web::http::StatusCode;
use actix_web::web::Data;
use actix_web::{middleware, web, App, HttpResponse, HttpServer};
use dotenv::dotenv;
use redis::Client;
use sea_orm::{Database, DatabaseConnection};

use crate::common::response::ErrorResponse;

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

    let db: DatabaseConnection = Database::connect(postgres_url)
        .await
        .expect("failed to connect postgres");

    let cache: Client = Client::open(redis_url)
        .expect("Invalid connection Url");

    let state: AppState = AppState {
        db: db.clone(),
        cache: cache.clone(),
    };

    HttpServer::new(move || {
        App::new()
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
        .bind(("127.0.0.1", 8080))?
        .run()
        .await
}

pub fn init(cfg: &mut web::ServiceConfig) {
    routes::user::user_handler(cfg);
    routes::auth::auth_handler(cfg);
    routes::index::index_handler(cfg);
}
