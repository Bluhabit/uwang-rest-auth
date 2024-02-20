use serde::{Deserialize, Serialize};
use validator::Validate;

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SignInAdminRequest {
    #[validate(email(code = "regex", message = "Email tidak boleh kosong."))]
    pub email: String,
    #[validate(length(min = 6, message = "Password tidak boleh kosong,minimal 6 karakter."))]
    pub password: String,
}
