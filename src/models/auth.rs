use serde::{Deserialize, Serialize};
use validator::Validate;

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct SignInBasicRequest {
    #[validate(email)]
    pub email: String,
    #[validate(length(min = 6))]
    pub password: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct SignInGoogleRequest {
    #[validate(length(min = 6))]
    pub token: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct SignUpBasicRequest {
    #[validate(email)]
    pub email: String,
    #[validate(length(min = 6))]
    pub password: String,
    #[validate(length(min = 1))]
    pub full_name: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignUpBasicRequest {
    #[validate(length(min = 1))]
    pub session_id: String,
    #[validate(length(min = 4))]
    pub otp: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignInBasicRequest {
    #[validate(length(min = 6))]
    pub session_id: String,
    #[validate(length(min = 4))]
    pub otp: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct SessionRedisModel {
    pub user_id: String,
    pub full_name: String,
    pub email: String,
    pub token: String,
}

#[derive(Debug, Serialize, Deserialize, Validate)]
pub struct OtpRedisModel {
    pub user_id: String,
    pub otp: String,
    pub session_id: String,
}
