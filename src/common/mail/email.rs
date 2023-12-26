use handlebars::Handlebars;
use lettre::{AsyncSmtpTransport, AsyncTransport, Message, Tokio1Executor};
use lettre::message::header::ContentType;
use lettre::transport::smtp::authentication::Credentials;
use lettre::transport::smtp::client::{Tls, TlsParameters};
use lettre::transport::smtp::response::Response;

use crate::common::mail::config::Config;

pub struct Email {
    email: String,
    name: String,
    from: String,
    config: Config,
}

impl Email {
    pub fn new(
        email: String,
        name: String,
    ) -> Self {
        let conf = Config::init();
        let from = format!("Bluhabit <{}>", conf.smtp_from.to_owned());
        Email {
            email,
            name,
            from,
            config: conf,
        }
    }

    fn new_transport(
        &self
    ) -> Result<AsyncSmtpTransport<Tokio1Executor>, lettre::transport::smtp::Error> {
        let creds = Credentials::new(
            self.config.smtp_user.to_owned(),
            self.config.smtp_pass.to_owned(),
        );

        let transport =
            AsyncSmtpTransport::<Tokio1Executor>::relay(
                &self.config.smtp_host.to_owned()
            )?.port(self.config.smtp_port)
                .credentials(creds)
                .build();

        Ok(transport)
    }

    async fn send_email(
        &self,
        html: String,
        subject: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let email = Message::builder()
            .to(
                format!("{} <{}>", self.name.as_str(), self.email.as_str())
                    .parse()
                    .unwrap(),
            )
            .reply_to(self.from.as_str().parse().unwrap())
            .from(self.from.as_str().parse().unwrap())
            .subject(subject)
            .header(ContentType::TEXT_HTML)
            .body(html)?;

        let transport = self.new_transport()?;

        let send = transport.send(email).await?;
        Ok(send)
    }

    pub async fn send_otp_sign_up_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let mut handlebars = Handlebars::new();
        handlebars.register_template_string("sign-in-basic-otp", include_str!("./templates/sign-in-basic-otp.hbs"))?;
        handlebars.register_template_string("styles", include_str!("./templates/partials/style.hbs"))?;
        handlebars.register_template_string("base", include_str!("./templates/layouts/base.hbs"))?;
        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });

        let content_template = handlebars.render("sign-in-basic-otp", &data)?;
        self.send_email(content_template, "Rahasia - OTP ")
            .await
    }

    pub async fn send_otp_sign_in_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let mut handlebars = Handlebars::new();
        handlebars.register_template_string("sign-in-basic-otp", include_str!("./templates/sign-in-basic-otp.hbs"))?;
        handlebars.register_template_string("styles", include_str!("./templates/partials/style.hbs"))?;
        handlebars.register_template_string("base", include_str!("./templates/layouts/base.hbs"))?;

        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });
        let content_template = handlebars.render("sign-in-basic-otp", &data)?;

        self.send_email(content_template, "Rahasia - OTP")
            .await
    }

    pub async fn send_otp_forgot_password_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let mut handlebars = Handlebars::new();
        handlebars.register_template_string("forgot-password", include_str!("./templates/forgot-password.hbs"))?;
        handlebars.register_template_string("styles", include_str!("./templates/partials/style.hbs"))?;
        handlebars.register_template_string("base", include_str!("./templates/layouts/base.hbs"))?;

        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });

        let content_template = handlebars.render("forgot-password", &data)?;

        self.send_email(content_template, "Rahasia - OTP")
            .await
    }
}