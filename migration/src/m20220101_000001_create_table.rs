use sea_orm::{EnumIter, Iterable};
use sea_orm_migration::prelude::*;

use crate::extension::postgres::Type;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts
        manager
            .create_type(
                Type::create()
                    .as_enum(Status::Table)
                    .values(Status::iter().skip(1))
                    .to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(AuthProvider::Table)
                    .values(AuthProvider::iter().skip(1))
                    .to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(VerificationType::Table)
                    .values(VerificationType::iter().skip(1))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserCredential::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserCredential::Id)
                            .string()
                            .not_null()
                            .primary_key(),
                    )
                    .col(
                        ColumnDef::new(UserCredential::Email)
                            .string()
                            .unique_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserCredential::FullName).string().not_null())
                    .col(ColumnDef::new(UserCredential::Password).string().not_null())
                    .col(
                        ColumnDef::new(UserCredential::Status)
                            .enumeration(Status::Table, Status::iter().skip(1))
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(UserCredential::AuthProvider)
                            .enumeration(AuthProvider::Table, AuthProvider::iter().skip(1))
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(UserCredential::CreatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserCredential::UpdatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserCredential::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserProfile::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(UserProfile::Id).string().primary_key())
                    .col(ColumnDef::new(UserProfile::Key).string().not_null())
                    .col(ColumnDef::new(UserProfile::Value).string().not_null())
                    .col(ColumnDef::new(UserProfile::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-profile")
                            .from(UserProfile::Table, UserProfile::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(
                        ColumnDef::new(UserProfile::CreatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(UserProfile::UpdatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserProfile::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserVerification::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserVerification::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserVerification::Code).string().not_null())
                    .col(
                        ColumnDef::new(UserVerification::VerificationType)
                            .enumeration(VerificationType::Table, VerificationType::iter().skip(1))
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserVerification::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-verification")
                            .from(UserVerification::Table, UserVerification::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(
                        ColumnDef::new(UserVerification::CreatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(UserVerification::UpdatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserVerification::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserReport::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserReport::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserReport::Reason).string().not_null())
                    .col(ColumnDef::new(UserReport::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-report")
                            .from(UserReport::Table, UserReport::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(UserReport::ReportedBy).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-report-by")
                            .from(UserReport::Table, UserReport::ReportedBy)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(UserReport::CreatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(UserReport::UpdatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(UserReport::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Admin::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Admin::Id).string().primary_key().not_null())
                    .col(ColumnDef::new(Admin::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-admin")
                            .from(Admin::Table, Admin::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(Admin::CreatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(Admin::UpdatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(Admin::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(SystemAccess::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(SystemAccess::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(SystemAccess::Name).string().not_null())
                    .col(ColumnDef::new(SystemAccess::Permission).string().not_null())
                    .col(ColumnDef::new(SystemAccess::Group).string().not_null())
                    .col(
                        ColumnDef::new(SystemAccess::CreatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(SystemAccess::UpdatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(ColumnDef::new(SystemAccess::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;
        manager
            .create_table(
                Table::create()
                    .table(AdminAccess::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(AdminAccess::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(AdminAccess::AdminId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-admin-user")
                            .from(AdminAccess::Table, AdminAccess::AdminId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(AdminAccess::AccessId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-admin-access")
                            .from(AdminAccess::Table, AdminAccess::AccessId)
                            .to(SystemAccess::Table, SystemAccess::Id),
                    )
                    .col(
                        ColumnDef::new(AdminAccess::CreatedAt)
                            .timestamp()
                            .not_null()
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(AdminAccess::UpdatedAt)
                            .timestamp()
                            .not_null(),
                    )
                    .col(ColumnDef::new(AdminAccess::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Device::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Device::Id).string().primary_key().not_null())
                    .col(ColumnDef::new(Device::DeviceName).string())
                    .col(ColumnDef::new(Device::DeviceId).string())
                    .col(ColumnDef::new(Device::DeviceOs).string())
                    .col(ColumnDef::new(Device::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-device")
                            .from(Device::Table, Device::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(Device::CreatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(Device::UpdatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(Device::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserLogin::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserLogin::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserLogin::Ip).string())
                    .col(ColumnDef::new(UserLogin::Token).string())
                    .col(ColumnDef::new(UserLogin::LoginAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(UserLogin::UserId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-login")
                            .from(UserLogin::Table, UserLogin::UserId)
                            .to(UserCredential::Table, UserCredential::Id),
                    )
                    .col(ColumnDef::new(UserLogin::DeviceId).string())
                    .foreign_key(
                        ForeignKey::create()
                            .name("fk-user-login-device")
                            .from(UserLogin::Table, UserLogin::DeviceId)
                            .to(Device::Table, Device::Id),
                    )
                    .col(ColumnDef::new(UserLogin::CreatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(UserLogin::UpdatedAt).timestamp_with_time_zone().not_null())
                    .col(ColumnDef::new(UserLogin::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;
        Ok(())
    }

    async fn down(&self, _: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts
        todo!();
    }
}

#[derive(DeriveIden)]
enum UserCredential {
    Table,
    Id,
    Email,
    Password,
    AuthProvider,
    Status,
    FullName,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(Iden, EnumIter)]
pub enum Status {
    Table,
    #[iden = "ACTIVE"]
    ACTIVE,
    #[iden = "INACTIVE"]
    INACTIVE,
    #[iden = "SUSPENDED"]
    SUSPENDED,
    #[iden = "WAITING_CONFIRMATION"]
    WAITING,
}

#[derive(Iden, EnumIter)]
pub enum AuthProvider {
    Table,
    #[iden = "GOOGLE"]
    GOOGLE,
    #[iden = "BASIC"]
    BASIC,
    #[iden = "FACEBOOK"]
    FACEBOOK,
    #[iden = "APPLE"]
    APPLE,
    #[iden = "GITHUB"]
    GITHUB,
    #[iden = "MICROSOFT"]
    MICROSOFT,
    #[iden = "TWITTER"]
    TWITTER,
}

#[derive(DeriveIden)]
enum UserVerification {
    Table,
    Id,
    Code,
    UserId,
    VerificationType,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(Iden, EnumIter)]
pub enum VerificationType {
    Table,
    #[iden = "OTP"]
    OTP,
    #[iden = "RESET"]
    RESET,
    #[iden = "ACTIVATION"]
    ACTIVATION,
}

#[derive(DeriveIden)]
enum UserProfile {
    Table,
    Id,
    Key,
    Value,
    UserId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum UserReport {
    Table,
    Id,
    UserId,
    ReportedBy,
    Reason,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Admin {
    Table,
    Id,
    UserId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum AdminAccess {
    Table,
    Id,
    AdminId,
    AccessId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum SystemAccess {
    Table,
    Id,
    Name,
    Group,
    Permission,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum UserLogin {
    Table,
    Id,
    UserId,
    DeviceId,
    Ip,
    Token,
    LoginAt,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Device {
    Table,
    Id,
    UserId,
    DeviceName,
    DeviceId,
    DeviceOs,
    CreatedAt,
    UpdatedAt,
    Deleted,
}
