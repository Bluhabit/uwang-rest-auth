use actix_web::{Responder, web};
use actix_web::web::Json;
use validator::Validate;

use crate::AppState;
use crate::common::response::{BaseResponse, ErrorResponse};
use crate::common::utils::get_readable_validation_message;
use crate::entity::{user_credential, user_profile};
use crate::models::user::{CompleteProfileRequest, UserCredentialResponse};
use crate::repositories::user::user::UserRepository;
use crate::request_filter::jwt_middleware::JwtMiddleware;

pub async fn complete_profile(
    state: web::Data<AppState>,
    jwt: JwtMiddleware,
    body: Json<CompleteProfileRequest>,
) -> Result<impl Responder, ErrorResponse> {
    let validate_body = body.validate();
    if validate_body.is_err() {
        let message = get_readable_validation_message(validate_body.err());
        return Err(ErrorResponse::bad_request(400, message));
    }

    let user_repository = UserRepository::init(&state);
    let find_user: Result<(user_credential::Model, Vec<user_profile::Model>), ErrorResponse> = user_repository
        .get_user_by_session_id(jwt.session_id)
        .await;

    if find_user.is_err() {
        return Err(find_user.unwrap_err());
    }
    let user = find_user.unwrap();

    let update_profile = user_repository
        .update_profile(
            user.0.clone(),
            user.1,
            body.0,
        ).await;

    if update_profile.is_err() {
        return Err(update_profile.unwrap_err());
    }
    Ok(Json(BaseResponse::success(200, Some(
        UserCredentialResponse::from_credential_with_profile(
            user.0,
            update_profile.unwrap(),
        )
    ), "Success".to_string())))
}