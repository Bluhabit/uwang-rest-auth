use ahash::HashMap;
use chrono::Duration;
use redis::{Client, RedisResult,Commands};
use sea_orm::{
    ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, ModelTrait, QueryFilter,
};
use serde_json::Value;
use uuid::Uuid;

use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::entity::{admin, admin_role, system_access, user_credential, user_profile};
use crate::models::admin::auth::{RolesResponse, SignInAdminRequest, SignInAdminResponse};
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

    ///== sign in email & password
    pub async fn sign_in_by_email(
        &mut self,
        email: &str,
        password: &str,
    ) -> Result<Option<Value>, ErrorResponse> {
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
        if data_user.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Tidak dapat menemukan akun".to_string(),
            ));
        }
        let data_user = data_user.as_ref().unwrap();

        let admin = data_user.find_related(admin::Entity).one(&self.db).await;

        if admin.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Akun Anda tidak terdaftar sebagai admin".to_string(),
            ));
        }

        let data_admin = admin.unwrap();
        if data_admin.is_none() {
            return Err(ErrorResponse::unauthorized(
                "Akun Anda tidak terdaftar sebagai admin".to_string(),
            ));
        }
        let data_admin = data_admin.unwrap();
        let admin_id = data_admin.clone().id;
        let verify_password = bcrypt::verify(password, &data_admin.password);
        if verify_password.is_err() {
            return Err(ErrorResponse::bad_request(
                401,
                "Email atau password salah.".to_string(),
            ));
        }

        let role = admin_role::Entity::find()
            .filter(admin_role::Column::AdminId.eq(admin_id))
            .find_also_related(system_access::Entity)
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        let role_response:Vec<RolesResponse> = role.iter().map(|role |{
            let r = role.0.to_owned();
            let access = role.1.to_owned().unwrap();
           return  RolesResponse {
                role_id : r.id.to_string(),
                role: access.clone().name,
                permission:access.clone().permission
            }
        }).collect();

        let profile = user_profile::Entity::find()
        .filter(user_profile::Column::UserId.eq(data_user.id))
        .all(&self.db)
        .await
        .unwrap_or(vec![]);
        let user_credential = SignInAdminResponse::from_credential_with_profile(data_user.clone(),profile);

        //set data login ke redis
        let session_id = Uuid::new_v4().to_string();
        let redis_util = RedisUtil::new(session_id.as_str());
        let redis_key = redis_util.create_key_session_sign_in_admin();
        let redis_connection = self.cache.get_connection();
        let session_redis: RedisResult<HashMap<String, String>> =
            redis_connection.unwrap().hgetall(redis_key.clone());
        if session_redis.is_err() {
            return Err(ErrorResponse::unauthorized(
                "Otp tidak valid atau sudah kadaluarsa [1]".to_string(),
            ));
        }
        let current_date = chrono::Utc::now().naive_local();
        let mut valid_at = current_date.timestamp();
        valid_at = (current_date + Duration::hours(1)).timestamp();

        let redis_connection = self.cache.get_connection();
            let _: RedisResult<String> = redis_connection.unwrap().hset_multiple(
                redis_key.clone(),
                &[
                    (
                        common::constant::REDIS_KEY_USER_ID_ADMIN,
                        format!("{}", data_user.id),
                    ),
                    (
                        common::constant::REDIS_KEY_TOKEN_ADMIN,
                        format!("{}","token ges" ),
                    ),
                    (
                        common::constant::REDIS_KEY_VALID_AT,
                        format!("{}", valid_at),
                    ),
                ],
            );
        //generate token
        // role_response dapat diakses di sini
        Ok(Some(serde_json::json!({
            "user": user_credential,
            "role": role_response,
            "token":""
        })))
    }
}
