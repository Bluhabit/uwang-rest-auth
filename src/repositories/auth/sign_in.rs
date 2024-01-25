use std::collections::HashMap;

use redis::{Client, Commands, RedisResult};
use sea_orm::{ActiveModelTrait, ColumnTrait, DatabaseConnection, EntityTrait, IntoActiveModel, QueryFilter};
use sea_orm::ActiveValue::Set;
use serde_json::Value;
use uuid::Uuid;

use crate::{AppState, common};
use crate::common::jwt::encode;
use crate::common::mail::email;
use crate::common::otp_generator::generate_otp;
use crate::common::redis_ext::RedisUtil;
use crate::common::response::ErrorResponse;
use crate::common::utils::{check_account_user_status_active, create_session_from_user, create_session_redis_from_user};
use crate::entity::{user_credential, user_profile};
use crate::entity::sea_orm_active_enums::{AuthProvider, UserStatus};
use crate::entity::user_credential::{Model as UserCredential, Model};
use crate::models::auth::{SessionRedisModel, SignInGoogleRequest};
use crate::models::user::UserCredentialResponse;

#[derive(Debug, Clone)]
pub struct SignInRepository {
    db: DatabaseConnection,
    cache: Client,
}

impl SignInRepository {
    pub fn init(app_state: &AppState) -> Self {
        let state = app_state.clone();
        SignInRepository {
            db: state.db,
            cache: state.cache,
        }
    }

    // sign in email & password
    pub async fn sign_in_by_email(
        &mut self,
        email: &str,
        password: &str,
    ) -> Result<String, ErrorResponse> {
        let redis_connection = self.cache
            .get_connection();
        let user_credential = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(email))
            .one(&self.db)
            .await;

        if user_credential.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [3]".to_string()));
        }
        let user_credential = user_credential.unwrap();
        if user_credential.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [4]".to_string()));
        }

        let data_user = user_credential.unwrap();
        let uuid = uuid::Uuid::new_v4().to_string();
        let sign_in_attempt_key = RedisUtil::new(&data_user.id).create_sign_in_attempt();
        let session_id = RedisUtil::new(uuid.as_str()).create_key_otp_sign_in();


        let mut sign_in_attempt: i32 = redis_connection
            .unwrap()
            .get(&sign_in_attempt_key)
            .unwrap_or("0".to_string())
            .as_str()
            .parse()
            .unwrap_or(0);

        if data_user.status == UserStatus::Locked {
            let email = email::Email::new(
                data_user.email,
                data_user.full_name.clone(),
            );

            let _ = email.send_otp_sign_in_basic(
                serde_json::json!({

                })
            ).await;
            return Err(ErrorResponse::bad_request(
                1007,
                "Akun kamu terkunci untuk sementara.".to_string(),
            ));
        }

        if bcrypt::verify(password, &data_user.password).is_err() {
            sign_in_attempt = sign_in_attempt + 1;
            let redis_connection = self.cache.get_connection();
            let _: RedisResult<String> = redis_connection
                .unwrap()
                .set(sign_in_attempt_key, format!("{}", sign_in_attempt).as_str());

            if sign_in_attempt >= 3 {
                let mut user_active_model = data_user.clone().into_active_model();
                user_active_model.status = Set(UserStatus::Locked);
                let _ = user_active_model.update(&self.db).await;
            }
            return Err(ErrorResponse::bad_request(
                1007,
                "Email atau password salah atau tidak sesuai".to_string(),
            ));
        }

        let check_user_status = check_account_user_status_active(&data_user);
        if check_user_status.is_err() {
            return Err(check_user_status.unwrap_err());
        }
        if data_user.auth_provider != AuthProvider::Basic {
            return Err(ErrorResponse::bad_request(1003, "Email sudah digunakan akun lain".to_string()));
        }


        let generate_otp = generate_otp();
        let current_date = chrono::Utc::now().naive_local().timestamp();

        let redis_connection = self.cache.get_connection();
        let saved_session_otp: Result<String, redis::RedisError> = redis_connection
            .unwrap()
            .hset_multiple(session_id.clone(), &[
                (common::constant::REDIS_KEY_OTP, generate_otp.as_str()),
                (common::constant::REDIS_KEY_USER_ID, data_user.id.as_str()),
                (common::constant::REDIS_KEY_OTP_ATTEMPT, "0"),
                (common::constant::REDIS_KEY_VALID_FROM, current_date.to_string().as_str())
            ]);

        let _: RedisResult<_> = self.cache
            .expire::<String, String>(session_id.clone(), common::constant::TTL_OTP_SIGN_IN);

        if saved_session_otp.is_err() {
            return Err(ErrorResponse::unauthorized(
                saved_session_otp.unwrap_err().to_string())
            );
        }

        //sending email
        let email = email::Email::new(
            data_user.email.clone(),
            data_user.full_name.clone(),
        );

        let _ = email.send_otp_sign_in_basic(
            serde_json::json!({

            })
        ).await;
        Ok(uuid)
    }
    //end sign in email & password
    //verify otp sign in
    pub async fn verify_otp_sign_in(
        &self,
        session_id: &str,
        request_otp: &str,
    ) -> Result<Option<Value>, ErrorResponse> {
        //obtain connection redis from main.rs via AppState
        let redis_connection = &self.cache
            .get_connection();
        if redis_connection.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                "Kami mengalami kendala menghubungi sumber data".to_string(),
            ));
        }
        //create key for otp sign in
        let redis_key = RedisUtil::new(session_id)
            .create_key_otp_sign_in();

        let redis_connection = self.cache.get_connection();
        let session_redis: RedisResult<HashMap<String, String>> = redis_connection
            .unwrap()
            .hgetall(redis_key.clone());
        if session_redis.is_err() {
            return Err(ErrorResponse::unauthorized("Otp tidak valid atau sudah kadaluarsa [1]".to_string()));
        }
        let _current_date = chrono::Utc::now().naive_local();

        let default_string = String::from("");
        let redis = session_redis.unwrap();

        let otp = redis.get(common::constant::REDIS_KEY_OTP)
            .unwrap_or(&default_string)
            .as_str();
        let user_id = redis.get(common::constant::REDIS_KEY_USER_ID)
            .unwrap_or(&default_string)
            .as_str();
        let mut attempt: i16 = redis.get(common::constant::REDIS_KEY_OTP_ATTEMPT)
            .unwrap_or(&"0".to_string())
            .parse()
            .unwrap_or(0);

        let user = user_credential::Entity::find_by_id(user_id)
            .one(&self.db)
            .await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [1]".to_string()));
        }
        let credential = user.unwrap();
        if credential.is_none() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [2]".to_string()));
        }
        let credential = credential.unwrap();

        if !request_otp.eq(otp) {
            //not valid update redis
            if attempt >= 4 {
                //update user status
                //delete from redis
                let mut model = credential.into_active_model();
                model.status = Set(UserStatus::Locked);
                let _ = model.update(&self.db).await;
                return Err(ErrorResponse::bad_request(1001, "Kamu sudah mencoba otp 3 kali.".to_string()));
            }

            attempt = attempt + 1;
            let redis_connection = self.cache.get_connection();
            let _: RedisResult<String> = redis_connection
                .unwrap()
                .hset_multiple(
                    session_id, &[
                        (common::constant::REDIS_KEY_OTP_ATTEMPT, attempt)
                    ],
                );
            return Err(ErrorResponse::unauthorized("Kode OTP Salah.".to_string()));
        }


        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_id))
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        let save_session = self
            .save_user_session_to_redis(&credential)
            .await;

        let credential_response = UserCredentialResponse::from_credential_with_profile(
            credential,
            profile,
        );
        let save_session = save_session.unwrap();

        let token = save_session.token;

        Ok(Some(
            serde_json::json!({
                "token":token,
                "user":credential_response
            })
        ))
    }
    //end verify otp sign in

    //sign in google
    pub async fn sign_in_google(
        &self,
        google_credential: &SignInGoogleRequest,
    ) -> Result<Option<Value>, ErrorResponse> {
        let google_credential = common::jwt::decode_google_token(google_credential.token.clone());
        if google_credential.is_err() {
            return Err(ErrorResponse::bad_request(
                1001,
                google_credential.err().unwrap().to_string(),
            ));
        }
        let google_account = google_credential.unwrap().claims;

        let user = user_credential::Entity::find()
            .filter(user_credential::Column::Email.eq(google_account.email.clone()))
            .one(&self.db).await;

        if user.is_err() {
            return Err(ErrorResponse::unauthorized("Akun tidak ditemukan [3]".to_string()));
        }
        let credential_result = user.unwrap();

        let user: Result<Model, ErrorResponse> = match credential_result {
            None => {
                let uuid = Uuid::new_v4();
                let current_date = chrono::Utc::now().naive_local();
                let prepare_data = user_credential::ActiveModel {
                    id: Set(uuid.to_string()),
                    email: Set(google_account.email.to_string()),
                    full_name: Set(google_account.given_name.to_string()),
                    password: Set("n/a".to_string()),
                    status: Set(UserStatus::Active),
                    auth_provider: Set(AuthProvider::Google),
                    created_at: Set(current_date),
                    updated_at: Set(current_date),
                    deleted: Set(false),
                    ..Default::default()
                };
                let saved_data = prepare_data.insert(&self.db).await;
                if saved_data.is_err() {
                    return Err(ErrorResponse::unauthorized("".to_string()));
                }
                let data = saved_data.unwrap();
                Ok(data)
            }
            Some(ref user_credential) => Ok(user_credential.clone())
        };

        let check_status = check_account_user_status_active(
            &user.unwrap()
        );
        if check_status.is_err() {
            return Err(check_status.unwrap_err());
        }

        if check_status.is_err() {
            return Err(
                ErrorResponse::bad_request(
                    1002,
                    "Akun belum terdaftar".to_string(),
                )
            );
        }

        let user_credential = check_status.unwrap();

        if user_credential.auth_provider != AuthProvider::Google {
            return Err(
                ErrorResponse::bad_request(
                    1003,
                    "Akun sudah digunakan".to_string(),
                )
            );
        }
        let profile = user_profile::Entity::find()
            .filter(user_profile::Column::UserId.eq(user_credential.id.clone()))
            .all(&self.db)
            .await
            .unwrap_or(vec![]);

        let saved_session = self.save_user_session_to_redis(
            &user_credential
        ).await;

        if saved_session.is_err() {
            let err = saved_session;
            return Err(err.unwrap_err());
        }
        let saved_session = saved_session.unwrap();

        let credential_response = UserCredentialResponse::from_credential_with_profile(
            user_credential,
            profile,
        );
        let token = saved_session.token;

        return Ok(Some(serde_json::json!({
            "token":token,
            "credential":credential_response
        })));
    }

    //end sign in google

    async fn save_user_session_to_redis(
        &self,
        user: &UserCredential,
    ) -> Result<SessionRedisModel, ErrorResponse> {
        let connection = self.cache
            .get_connection();
        if connection.is_err() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let session_key = RedisUtil::new(user.id.as_str())
            .create_key_session_sign_in();


        let generate_token = encode(user.id.clone());
        if generate_token.is_none() {
            return Err(ErrorResponse::bad_request(400, "Gagal membuat sesi".to_string()));
        }

        let _: Result<String, redis::RedisError> = connection
            .unwrap()
            .hset_multiple(
                session_key,
                &*create_session_redis_from_user(
                    user.clone(),
                    generate_token
                        .clone()
                        .unwrap(),
                ),
            );

        Ok(create_session_from_user(
            user.to_owned(),
            generate_token.unwrap(),
        ))
    }
}
