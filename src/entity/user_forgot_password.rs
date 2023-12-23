use sea_orm::DatabaseConnection;
use sea_orm::prelude::DateTimeWithTimeZone;

pub struct PasswordReset {
    pub id: String,
    pub user_id: String,
    pub token: String,
    pub created_at: DateTimeWithTimeZone,
    pub expires_at: DateTimeWithTimeZone
}

fn reset_email(to: &str, token: &str) {

}

async fn forgot_password(email: String, db: &DatabaseConnection) -> Result<() {
    let user =
}