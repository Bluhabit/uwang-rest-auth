use std::collections::HashMap;

use bcrypt::{DEFAULT_COST, hash};
use chrono::FixedOffset;
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, ModelTrait, QueryFilter, sea_query, TryIntoModel};
use sea_orm::ActiveValue::Set;
use sea_orm::prelude::DateTime;
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::otp_generator::generate_otp;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::check_account_status_active_user;
use crate::entity::{user_credential, user_profile, user_verification};
use crate::entity::prelude::UserProfile;
use crate::entity::user_profile::Model;
use crate::models::auth::OtpRedisModel;
use crate::models::user::CompleteProfileRequest;
use crate::models::utils::create_user_verification;

#[derive(Debug, Clone)]
pub struct UserRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl UserRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        UserRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    // complete profile
    pub async fn get_user_by_session_id(
        &self, session_id: String,
    ) -> Result<(user_credential::Model, Vec<user_profile::Model>), ErrorResponse> {
        let redis_Connection = self
            .cache.get_connection();

        if redis_Connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal menghubungi server [1]".to_string()));
        }
        let mut connection = redis_Connection.unwrap();

        let session_key = RedisUtil::new(session_id.as_str())
            .create_key_session_sign_in();


        let session: RedisResult<HashMap<String, String>> = connection.hgetall(session_key);
        if session.is_err() {
            return Err(ErrorResponse::unauthorized("Sesi sudah berakhir silahkan login kembali [1]".to_string()));
        }
        let user_session = session.unwrap();
        let user_id_redis = user_session.get(common::constant::REDIS_KEY_USER_ID);
        if user_id_redis.is_none() {
            return Err(ErrorResponse::unauthorized("Sesi sudah berakhir silahkan login kembali [2]".to_string()));
        }

        let user = user_credential::Entity::find_by_id(user_id_redis.clone().unwrap())
            .one(&self.db)
            .await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Sesi sudah berakhir silahkan login kembali [3]".to_string()));
        }

        let credential = user.unwrap();

        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("Sesi sudah berakhir silahkan login kembali [4]".to_string()));
        }


        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_id_redis.unwrap()))
            .all(&self.db)
            .await;

        let mut profile_data: Vec<Model> = Vec::new();
        if profile.is_err() {
            profile_data = vec![];
        } else {
            profile_data = profile.unwrap();
        }

        Ok((credential.unwrap(), profile_data))
    }

    pub async fn update_profile(
        &self,
        user: user_credential::Model,
        profile: Vec<Model>,
        body: CompleteProfileRequest,
    ) -> Result<Vec<Model>, ErrorResponse> {
        let current_date = chrono::DateTime::<FixedOffset>::default().naive_local();
        let mut insert_data: Vec<user_profile::ActiveModel> = Vec::new();

        let username = profile.iter().find(|model| model.key == "username");
        let uuid = Uuid::new_v4();
        match username {
            None => {
                let data = user_profile::ActiveModel {
                    id: Set(uuid.to_string()),
                    key: Set("username".to_string()),
                    value: Set(body.username),
                    user_id: Set(Some(user.id.clone())),
                    created_at: Set(current_date.clone()),
                    updated_at: Set(current_date.clone()),
                    deleted: Set(false),
                    ..Default::default()
                };
                insert_data.push(data);
            }
            Some(model) => {
                let mut data = model.clone().into_active_model();
                data.value = Set(body.username);
                insert_data.push(data);
            }
        }
        let avatar = profile.iter().find(|model| model.key == "avatar");
        let uuid = Uuid::new_v4();
        match avatar {
            None => {
                let data = user_profile::ActiveModel {
                    id: Set(uuid.to_string()),
                    key: Set("avatar".to_string()),
                    value: Set(body.avatar),
                    user_id: Set(Some(user.id.clone())),
                    created_at: Set(current_date.clone()),
                    updated_at: Set(current_date.clone()),
                    deleted: Set(false),
                    ..Default::default()
                };
                insert_data.push(data);
            }
            Some(model) => {
                let mut data = model.clone().into_active_model();
                data.value = Set(body.avatar);
                insert_data.push(data);
            }
        }
        let personal_preferences = profile.iter().find(|model| model.key == "personal_preferences");
        let uuid = Uuid::new_v4();
        match personal_preferences {
            None => {
                let data = user_profile::ActiveModel {
                    id: Set(uuid.to_string()),
                    key: Set("personal_preferences".to_string()),
                    value: Set(body.personal_preferences.join(",")),
                    user_id: Set(Some(user.id.clone())),
                    created_at: Set(current_date.clone()),
                    updated_at: Set(current_date.clone()),
                    deleted: Set(false),
                    ..Default::default()
                };
                insert_data.push(data);
            }
            Some(model) => {
                let mut data = model.clone().into_active_model();
                data.value = Set(body.personal_preferences.join(","));
                insert_data.push(data);
            }
        }
        let date_of_birth = profile.iter().find(|model| model.key == "date_of_birth");
        let uuid = Uuid::new_v4();
        match date_of_birth {
            None => {
                let data = user_profile::ActiveModel {
                    id: Set(uuid.to_string()),
                    key: Set("date_of_birth".to_string()),
                    value: Set(body.date_of_birth),
                    user_id: Set(Some(user.id.clone())),
                    created_at: Set(current_date.clone()),
                    updated_at: Set(current_date.clone()),
                    deleted: Set(false),
                    ..Default::default()
                };
                insert_data.push(data);
            }
            Some(model) => {
                let mut data = model.clone().into_active_model();
                data.value = Set(body.date_of_birth);
                insert_data.push(data);
            }
        }
        let saved = user_profile::Entity::insert_many(insert_data.clone())
            .on_conflict(
                sea_query::OnConflict::column(user_profile::Column::Id)
                    .update_column(user_profile::Column::Key)
                    .update_column(user_profile::Column::Value)
                    .to_owned()
            )
            .exec(&self.db)
            .await;
        if saved.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal menyimpan profile".to_string()));
        }

        let transform: Vec<user_profile::Model> = insert_data
            .clone()
            .iter()
            .map(|a| {
                return a.clone().try_into_model().unwrap();
            })
            .collect();

        Ok(transform)
    }


    //end complete profile
}