use handlebars::Handlebars;
use mail_send::Credentials as Creds;
use mail_send::mail_builder::headers::address::Address;
use mail_send::mail_builder::MessageBuilder;
use mail_send::SmtpClientBuilder;

use crate::common::mail::config::Config;
use crate::common::response::ErrorResponse;

pub struct Email {
    to: String,
    name: String,
    from: String,
    config: Config,
}

const FORGOT_PASSWORD_OTP:&str = "forgot-password";
const SIGN_IN_BASIC_OTP:&str="sign-in-basic-otp";
const SIGN_UP_BASIC_OTP:&str="sign-up-basic-otp";
const WELCOMING_USER:&str="welcoming-user";
const FRAUD_ACTIVITY:&str="fraud-activity";
impl Email {
    pub fn new(
        to: String,
        name: String,
    ) -> Self {
        let conf = Config::init();
        let from = conf.smtp_from.to_owned();
        Email {
            to,
            name,
            from,
            config: conf,
        }
    }

    pub async fn send_otp_sign_up_basic(
        &self,
        data:serde_json::Value
    ) -> Result<String, ErrorResponse> {
        self.send_by_mail_send(
            "[Uwang] - Konfirmasi OTP",
            SIGN_UP_BASIC_OTP,
            data,
        ).await
    }

    pub async fn send_otp_fraud_activity(
        &self,
        data:serde_json::Value
    ) -> Result<String, ErrorResponse> {

        self.send_by_mail_send(
            "Aktivitas Mencurigakan Pada Akun Anda",
            FRAUD_ACTIVITY,
            data,
        ).await
    }

    pub async fn send_otp_sign_in_basic(
        &self,
        data:serde_json::Value,
    ) -> Result<String, ErrorResponse> {

        self.send_by_mail_send(
            "[Uwang] - Konfirmasi OTP",
            SIGN_IN_BASIC_OTP,
            data,
        ).await
    }

    pub async fn send_otp_forgot_password(
        &self,
        data: serde_json::Value
    ) -> Result<String, ErrorResponse> {
        self.send_by_mail_send(
            "[Uwang] - Konfirmasi OTP",
            FORGOT_PASSWORD_OTP,
            data,
        ).await
    }

    pub async fn send_welcoming_user(
        &self,
        data: serde_json::Value
    ) -> Result<String, ErrorResponse> {
        self.send_by_mail_send(
            "Selamat bergabung di Uwang!",
            WELCOMING_USER,
            data,
        ).await
    }

    async fn send_by_mail_send(
        &self,
        subject: &str,
        template_name: &str,
        data: serde_json::Value,
    ) -> Result<String, ErrorResponse> {
        let mut handlebars = Handlebars::new();
        handlebars.register_template_string(FORGOT_PASSWORD_OTP, include_str!("./templates/forgot-password.hbs")).expect("Panic forgot");
        handlebars.register_template_string(FRAUD_ACTIVITY, include_str!("./templates/fraud-activity.hbs")).expect("Panic fraud");
        handlebars.register_template_string(SIGN_IN_BASIC_OTP, include_str!("./templates/sign-in-basic-otp.hbs")).expect("Panic sign in");
        handlebars.register_template_string(WELCOMING_USER, include_str!("./templates/welcoming-user.hbs")).expect("Panic welcoming");
        handlebars.register_template_string(SIGN_UP_BASIC_OTP, include_str!("./templates/sign-up-basic-otp.hbs")).expect("Panic sign up");
        handlebars.register_template_string("styles", include_str!("./templates/partials/style.hbs")).expect("Panic style");
        handlebars.register_template_string("base", include_str!("./templates/layouts/base.hbs")).expect("Panic base");


        let content_template = handlebars.render(template_name, &data).expect("Panic render");

        let message = MessageBuilder::new()
            .from(("Bluhabit",self.from.as_str()))
            .to(vec![Address::new_address(Some(&self.name), &*self.to.as_str())])
            .subject(subject)
            .html_body(content_template)
            .text_body(subject);

        let mut connection = SmtpClientBuilder::new(&self.config.smtp_host, self.config.smtp_port)
            .implicit_tls(true)
            .credentials(Creds::new(&self.config.smtp_user, &self.config.smtp_pass))
            .connect()
            .await
            .unwrap();


        let _ = connection
            .send(message)
            .await
            .unwrap();

        Ok("Success".to_string())
    }
}