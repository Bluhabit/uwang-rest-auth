use handlebars::Handlebars;
use lettre::{AsyncSmtpTransport, AsyncTransport, Message, Tokio1Executor};
use lettre::message::header::ContentType;
use lettre::transport::smtp::authentication::Credentials;
use lettre::transport::smtp::response::Response;
use mail_send::mail_builder::headers::address::Address;
use mail_send::mail_builder::MessageBuilder;
use mail_send::SmtpClientBuilder;
use mail_send::Credentials as Creds;

use crate::common::mail::config::Config;
use crate::common::response::ErrorResponse;

pub struct Email {
    to: String,
    name: String,
    from: String,
    config: Config,
}

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
                format!("{} <{}>", self.name.as_str(), self.to.as_str())
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
    ) -> Result<String, ErrorResponse> {
        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });

        self.send_by_mail_send(
            "Rahasia - OTP",
            "sign-up-basic-otp",
            &data,
        ).await
    }

    pub async fn send_otp_sign_in_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<String, ErrorResponse> {
        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });

        self.send_by_mail_send(
            "Rahasia - OTP",
            "sign-in-basic-otp",
            &data,
        ).await
    }

    pub async fn send_otp_forgot_password_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<String, ErrorResponse> {
        let data = serde_json::json!({
            "name": name,
            "otp_code": otp_code
        });

        self.send_by_mail_send(
            "Rahasia - OTP",
            "forgot-password",
            &data,
        ).await
    }

    async fn send_by_mail_send(
        &self,
        subject: &str,
        template_name: &str,
        data: &serde_json::Value,
    ) -> Result<String, ErrorResponse> {
        let mut handlebars = Handlebars::new();
        handlebars.register_template_string("forgot-password", include_str!("./templates/forgot-password.hbs")).expect("Panic forgot");
        handlebars.register_template_string("sign-in-basic-otp", include_str!("./templates/sign-in-basic-otp.hbs")).expect("Panic sign in");
        handlebars.register_template_string("sign-up-basic-otp", include_str!("./templates/sign-up-basic-otp.hbs")).expect("Panic sign up");
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


        let send = connection
            .send(message)
            .await
            .unwrap();

        Ok("Success".to_string())
    }
}