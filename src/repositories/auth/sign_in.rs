use redis::Client;
use sea_orm::{DatabaseConnection};

use crate::AppState;

#[derive(Debug, Clone)]
pub struct SignInRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignInRepository {
            db: state.db,
            cache: state.cache,
        }
    }
}
