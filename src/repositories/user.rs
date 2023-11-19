use sea_orm::DatabaseConnection;

#[derive(Debug, Clone)]
pub struct UserRepository {
    pub db_conn: DatabaseConnection,
}

impl UserRepository {}
