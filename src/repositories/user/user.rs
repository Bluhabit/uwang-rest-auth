use std::collections::HashMap;

use bcrypt::{DEFAULT_COST, hash};
use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter};
use sea_orm::ActiveValue::Set;

use crate::{AppState, common};
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::check_account_status_active_user;
use crate::entity::{user_credential, user_verification};
use crate::models::auth::OtpRedisModel;
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

    //get user

    //end get user
}