use chrono::FixedOffset;
use sea_orm::ActiveValue::Set;
use uuid::Uuid;
use crate::common::otp_generator::generate_otp;
use crate::entity::{user_credential, user_verification};
use crate::entity::sea_orm_active_enums::VerificationType;

pub fn create_user_verification(
    credential:user_credential::Model
)-> user_verification::ActiveModel{
    let current_date = chrono::DateTime::<FixedOffset>::default().naive_local();
    let otp = generate_otp();
    let uuid = Uuid::new_v4();

    user_verification::ActiveModel {
        id: Set(uuid.to_string()),
        code: Set(otp.clone().to_string()),
        verification_type: Set(VerificationType::Otp),
        user_id: Set(Some(credential.id.to_string())),
        created_at: Set(current_date),
        updated_at: Set(current_date),
        deleted: Set(false),
        ..Default::default()
    }
}