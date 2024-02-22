use sea_orm::Iterable;
use sea_orm_migration::prelude::*;
use uuid::uuid;

use crate::entity::*;
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
                    .as_enum(UserStatus::Table)
                    .values(UserStatus::iter().skip(1))
                    .to_owned(),
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(UserGender::Table)
                    .values(UserGender::iter().skip(1))
                    .to_owned(),
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(AuthProvider::Table)
                    .values(AuthProvider::iter().skip(1))
                    .to_owned(),
            )
            .await?;


        manager
            .create_table(
                Table::create()
                    .table(UserCredential::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserCredential::Id)
                            .uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(
                        ColumnDef::new(UserCredential::Email)
                            .string()
                            .unique_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(UserCredential::Password).string().not_null())
                    .col(ColumnDef::new(UserCredential::Username).string().not_null())
                    .col(ColumnDef::new(UserCredential::FullName).string().not_null())
                    .col(ColumnDef::new(UserCredential::DateOfBirth).date().null())
                    .col(
                        ColumnDef::new(UserCredential::Gender)
                            .enumeration(UserGender::Table, UserGender::iter().skip(1))
                            .null(),
                    )
                    .col(
                        ColumnDef::new(UserCredential::Status)
                            .enumeration(UserStatus::Table, UserStatus::iter().skip(1))
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
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserCredential::Deleted)
                            .boolean()
                            .not_null()
                            .default(false),
                    )
                    .to_owned(),
            )
            .await?;


        manager
            .create_index(
                Index::create()
                    .name("index-user-credential-email")
                    .table(UserCredential::Table)
                    .col(UserCredential::Email)
                    .to_owned(),
            )
            .await?;

        Ok(())
    }

    async fn down(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts
        Ok(())
    }
}