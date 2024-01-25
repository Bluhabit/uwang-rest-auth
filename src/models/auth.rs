use serde::{Deserialize, Serialize};
use validator::Validate;

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct SignInBasicRequest {
    #[validate(email(code="regex", message="Email tidak boleh kosong."))]
    pub email:String,
    #[validate(length(min=6,message="Password tidak boleh kosong,minimal 6 karakter."))]
    pub password:String
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignInBasicRequest {
    #[validate(length(min = 10,message="Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4, message="Otp tidak boleh kosong, minimal 4 karakter."))]
    pub otp: String,
}

#[derive(Debug, Clone,Serialize, Deserialize, Validate)]
pub struct SignInGoogleRequest {
    #[validate(length(min = 10, message="Token tidak boleh kosong, minimal 10 karakter."))]
    pub token: String,
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct SignUpBasicRequest {
    #[validate(email(code="regex",message="Email tidak valid."))]
    pub email: String,
    #[validate(length(min = 6,message="Password tidak boleh kosong, minimal 6 karakter."))]
    pub password: String
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignUpBasicRequest {
    #[validate(length(min = 10,message="Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4))]
    pub otp: String,
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct ForgotPasswordRequest {
    #[validate(email(code="regex",message="Email tidak valid"))]
    pub email: String
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpForgotPasswordRequest {
    #[validate(length(min = 10,message="Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4,message="Otp tidak boleh kosong, minimal 4 karakter."))]
    pub otp: String,
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct SetForgotPasswordRequest {
    #[validate(length(min=6,message="Password tidak boleh kosong, minimal 6 karakter."))]
    pub password: String,
    #[validate(length(min=10,message="Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct SessionRedisModel {
    pub user_id: String,
    pub full_name: String,
    pub email: String,
    pub token: String,
}

#[derive(Debug,Clone, Serialize, Deserialize, Validate)]
pub struct OtpRedisModel {
    pub user_id: String,
    pub otp: String,
    pub session_id: String,
    pub attempt: String
}
