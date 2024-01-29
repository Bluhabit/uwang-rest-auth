pub struct RedisUtil {
    pub value: String,
    environment: String,
}

impl RedisUtil {
    pub fn new(value: &str) -> Self {
        let env = std::env::var("ENVIRONMENT")
            .unwrap_or(String::from("dev"));
        RedisUtil {
            value: value.to_string(),
            environment: env,
        }
    }

    pub fn create_sign_in_attempt(&self)->String{
        return format!(
            "{}:attempt:sign-in:{}",
            self.environment,
            self.value
        );
    }
    pub fn create_key_otp_sign_in(
        &self
    ) -> String {
        return format!(
            "{}:otp:sign-in:{}",
            self.environment,
            self.value
        );
    }

    pub fn create_key_otp_sign_up(
        &self
    ) -> String {
        return format!(
            "{}:otp:sign-up:{}",
            self.environment,
            self.value
        );
    }

    pub fn create_key_otp_forgot_password(
        &self
    ) -> String {
        return format!(
            "{}:otp:forgot-password:{}",
            self.environment,
            self.value
        );
    }
    pub fn create_key_session_sign_in(
        &self
    ) -> String {
        return format!(
            "{}:sign-in:{}",
            self.environment,
            self.value
        );
    }

    pub fn create_key_session_forgot_password(
        &self
    ) -> String {
        return format!(
            "{}:sign-in:{}",
            self.environment,
            self.value
        );
    }
}
