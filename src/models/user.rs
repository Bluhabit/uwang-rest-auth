use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
pub struct UserInfoRequest {
    pub name: String,
    pub value: String,
}

#[derive(Serialize, Deserialize)]
pub struct AddUserInfoRequest {
    pub user_id: String,
    pub user_info: Vec<UserInfoRequest>,
}

#[derive(Serialize, Deserialize)]
pub struct SignUpRequest {
    pub full_name: String,
    pub email: String,
    pub password: String,
}
