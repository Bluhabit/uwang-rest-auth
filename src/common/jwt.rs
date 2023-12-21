use std::collections::HashSet;
use std::ops::Add;
use std::string::ToString;

use chrono::{Duration, Local};
use jsonwebtoken::{Algorithm, DecodingKey, EncodingKey, errors::Error as JwtError, Header, TokenData, Validation};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct Claims {
    pub iss: String,
    pub sub: String,
    pub iat: i64,
    pub exp: i64,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Payload {
    pub iss: String,
    pub azp: String,
    pub aud: String,
    pub sub: String,
    pub email: String,
    pub email_verified:bool,
    pub name:String,
    pub picture:String,
    pub given_name:String,
    pub family_name:String,
    pub locale:String,
    pub iat:i32,
    pub exp:i32
}

const JWT_SECRET_KEY: &str = "JWT_SECRET";
const JWT_SECRET_KEY_DEFAULT: &str = "triandamai";
const ISS: &str = "bluhabit.id";

pub fn encode(
    sub: String
) -> Option<String> {
    let secret = std::env::var(JWT_SECRET_KEY)
        .unwrap_or(JWT_SECRET_KEY_DEFAULT.to_string());
    let exp = Local::now().add(Duration::hours(1)).timestamp();
    let claims = Claims {
        iss: ISS.to_string(),
        sub,
        iat: Local::now().timestamp(),
        exp,
    };

    let token = jsonwebtoken::encode(
        &Header::new(Algorithm::HS256),
        &claims,
        &EncodingKey::from_secret(secret.as_ref()),
    ).unwrap();
    Some(token)
}

pub fn decode(
    token: String
) -> Result<TokenData<Claims>, jsonwebtoken::errors::Error> {
    let secret = std::env::var(JWT_SECRET_KEY)
        .unwrap_or(JWT_SECRET_KEY_DEFAULT.to_string());
    let decoded: Result<TokenData<Claims>, JwtError> = jsonwebtoken::decode::<Claims>(
        &token,
        &DecodingKey::from_secret(secret.as_ref()),
        &Validation::new(Algorithm::HS256),
    );
    decoded
}

pub fn decode_google_token(
    token:String
)->Result<TokenData<Payload>,jsonwebtoken::errors::Error>{
    let key = DecodingKey::from_secret(&[]);
    let mut validation = Validation::new(Algorithm::HS256);
    validation.insecure_disable_signature_validation();
    validation.validate_exp = false;
    validation.aud = Some(HashSet::from(["616208190167-aget8lort8gj59osgs4doe9g9i5bnhfj.apps.googleusercontent.com".to_string()]));

    let decoded = jsonwebtoken::decode::<Payload>(
        &token,
        &key,
        &validation
    );
    if decoded.is_err(){
        return Err(decoded.err().unwrap())
    }

    let token = decoded.unwrap();
    println!("{:?}",token.claims);
    Ok(token)
}