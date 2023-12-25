use handlebars::Handlebars;
use lettre::{AsyncSmtpTransport, AsyncTransport, Message, Tokio1Executor};
use lettre::message::header::ContentType;
use lettre::transport::smtp::authentication::Credentials;
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
            )?
                .port(self.config.smtp_port)
                .credentials(creds)
                .build();

        Ok(transport)
    }

    fn render_template(
        &self,
        template_name: &str,
        data: &serde_json::Value,
    ) -> Result<String, handlebars::RenderError> {
        let mut handlebars = Handlebars::new();
        handlebars
            .register_template_file(template_name, &format!("./templates/{}.hbs", template_name))?;
        handlebars.register_template_file("styles", "./templates/partials/style.hbs")?;
        handlebars.register_template_file("base", "./templates/layouts/base.hbs")?;

        let content_template = handlebars.render(template_name, &data)?;

        Ok(content_template)
    }

    async fn send_email(
        &self,
        template_name: &str,
        subject: &str,
        data: &serde_json::Value,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let html_template = self.render_template(template_name, data)?;
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
            .body(html_template)?;

        let transport = self.new_transport()?;

        let send = transport.send(email).await?;

        let log = send.clone();
        println!("Result {}",log.code());
        println!("Message {}",log.message().map(|s|s.to_string()).collect());
        Ok(send)
    }

    pub async fn send_otp_sign_up_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let data = serde_json::json!({
            "full_name": name,
            "otp_code": otp_code
        });
        self.send_email("sign-up-basic-otp", "Rahasia - OTP ", &data)
            .await
    }

    pub async fn send_otp_sign_in_basic(
        &self,
        name: &str,
        otp_code: &str,
    ) -> Result<Response, Box<dyn std::error::Error>> {
        let data = serde_json::json!({
            "full_name": name,
            "otp_code": otp_code
        });
        self.send_email("sign-in-basic-otp", "Rahasia - OTP", &data)
            .await
    }
}