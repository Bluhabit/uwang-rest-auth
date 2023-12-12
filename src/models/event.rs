use serde::{Deserialize, Serialize};
use validator::Validate;

#[derive(Debug,Serialize,Deserialize,Validate)]
pub struct SendEventRequest {
    #[validate(length(min=1))]
    pub event_name:String,
    #[validate(length(min=3))]
    pub message:String,
    #[validate(length(min=1))]
    pub to:String
}