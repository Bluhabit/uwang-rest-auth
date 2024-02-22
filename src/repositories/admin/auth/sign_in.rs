use chrono::{Duration, Locale};
use std::collections::HashMap;

use redis::{Client, Commands, RedisResult};
use sea_orm::ActiveValue::Set;
use sea_orm::{
    ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter,
};
use serde_json::Value;
use uuid::Uuid;

use crate::common::mail::email;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::check_account_user_status_active;
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::Model;
use crate::entity::{admin, user_credential};
use crate::models::admin::auth::SignInAdminRequest;
use crate::{common, AppState};

#[derive(Debug, Clone)]
pub struct SignInAdminRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInAdminRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignInAdminRepository {
            db: state.db,
            cache: state.cache,
        }
    }
}

 ///== sign in email & password
 pub async fn sign_in_by_email(
  &mut self,
  email: &str,
  password: &str,
) -> Result<String, ErrorResponse> {
  let redis_connection = &self.cache.get_connection();
  if redis_connection.is_err() {
      return Err(ErrorResponse::bad_request(
          1001,
          "Kami mengalami kendala menghubungi sumber data".to_string(),
      ));
  }
  let user_credential = user_credential::Entity::find()
      .filter(user_credential::Column::Email.eq(email))
      .one(&self.db)
      .await;

  if user_credential.is_err() {
        return Err(ErrorResponse::unauthorized(
            "Tidak dapat menemukan akun".to_string(),
        ));
    }
  let data_user = user_credential.unwrap();
  let user_id = data_user.id;

  let admin = admin::Entity::find()
    .filter(admin::Column::UserId.eq(user_id))
    .one(&self.db)
    .await;

  if admin.is_err() {
      return Err(ErrorResponse::unauthorized(
          "Akun Anda tidak terdaftar sebagai admin".to_string(),
      ));
  }
  let data_admin = admin.unwrap();
  let verify_password = bcrypt::verify(password, &data_admin.password);
  if verify_password.is_err() {
    return Err(ErrorResponse::bad_request(
        401,
        "Email atau password salah.".to_string(),
    ));
  }
  
}
/// == end sign in email & password
