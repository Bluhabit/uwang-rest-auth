use sea_orm::{EnumIter, Iterable};
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
            .create_table(
                Table::create()
                    .table(UserProfile::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserProfile::Id)
                            .uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(ColumnDef::new(UserProfile::Key).string().not_null())
                    .col(ColumnDef::new(UserProfile::Value).string().not_null())
                    .col(ColumnDef::new(UserProfile::UserId).uuid())
                    .col(
                        ColumnDef::new(UserProfile::CreatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserProfile::UpdatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserProfile::Deleted)
                            .boolean()
                            .not_null()
                            .default(false),
                    )
                    .to_owned(),
            )
            .await?;

        manager
            .create_foreign_key(
                ForeignKey::create()
                    .name("fk-user-profile")
                    .from(UserProfile::Table, UserProfile::UserId)
                    .to(UserCredential::Table, UserCredential::Id)
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