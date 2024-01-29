pub const REDIS_KEY_OTP: &str = "otp";
pub const REDIS_KEY_OTP_ATTEMPT: &str = "otp_attempt";
pub const REDIS_KEY_USER_ID: &str = "user_id";
pub const REDIS_KEY_TOKEN: &str = "token";
pub const REDIS_KEY_EMAIL: &str = "email";
pub const REDIS_KEY_FULL_NAME: &str = "full_name";

pub const REDIS_KEY_VALID_FROM:&str="valid_from";

pub const TTL_OTP_SIGN_IN: i64 = 3600;
pub const TTL_OTP_SIGN_UP: i64 = 3600;
pub const TTL_OTP_FORGOT_PASSWORD: i64 = 3600;
pub const TTL_SESSION_FORGOT_PASSWORD: i64 = 3600;