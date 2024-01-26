use serde::{Deserialize, Serialize};
use validator::{Validate, ValidationError};

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SignInBasicRequest {
    #[validate(email(code = "regex", message = "Email tidak boleh kosong."))]
    pub email: String,
    #[validate(length(min = 6, message = "Password tidak boleh kosong,minimal 6 karakter."))]
    pub password: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignInBasicRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4, message = "Otp tidak boleh kosong, minimal 4 karakter."))]
    pub otp: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct ResendOtpSignInBasicRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SignInGoogleRequest {
    #[validate(length(min = 10, message = "Token tidak boleh kosong, minimal 10 karakter."))]
    pub token: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SignUpBasicRequest {
    #[validate(email(code = "regex", message = "Email tidak valid."))]
    pub email: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpSignUpBasicRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4))]
    pub otp: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct ResendOtpSignUpBasicRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct CompleteProfileSignUpBasicRequest {
    #[validate(length(min = 10, message = "Session id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 1, message = "Gender tidak boleh kosong."))]
    pub gender: String,
    #[validate(length(min = 3, message = "Nama lengkap tidak boleh kosong, minimal 3 karakter."))]
    pub full_name: String,
    #[validate(length(min = 2, message = "Tanggal lahir tidak boleh kosong."), custom(function= "validate_date_of_birth",code="dob"))]
    pub date_of_birth: String,
}

fn validate_date_of_birth(date_of_birth: &str) -> Result<(), ValidationError> {
    let parse = chrono::NaiveDate::parse_from_str(date_of_birth, "%d-%m-%Y");
    if parse.is_err() {
        return Err(ValidationError::new("dob"));
    }
    Ok(())
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SetPasswordSignUpBasicRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 8, message = "Passwrod tidak boleh kosong, minimal 8 karakter."))]
    pub new_password: String,
}


#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct ForgotPasswordRequest {
    #[validate(email(code = "regex", message = "Email tidak valid"))]
    pub email: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct VerifyOtpForgotPasswordRequest {
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
    #[validate(length(min = 4, message = "Otp tidak boleh kosong, minimal 4 karakter."))]
    pub otp: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SetForgotPasswordRequest {
    #[validate(length(min = 6, message = "Password tidak boleh kosong, minimal 6 karakter."))]
    pub password: String,
    #[validate(length(min = 10, message = "Session Id tidak boleh kosong, minimal 10 karakter."))]
    pub session_id: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct SessionRedisModel {
    pub user_id: String,
    pub full_name: String,
    pub email: String,
    pub token: String,
}

#[derive(Debug, Clone, Serialize, Deserialize, Validate)]
pub struct OtpRedisModel {
    pub user_id: String,
    pub otp: String,
    pub session_id: String,
    pub attempt: String,
}
