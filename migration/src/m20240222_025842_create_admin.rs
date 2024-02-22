use sea_orm_migration::prelude::*;
use crate::entity::{*};

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts
        manager
            .create_table(
                Table::create()
                    .table(Admin::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Admin::Id)
                        .uuid()
                        .primary_key()
                        .not_null()
                        .default(uuid::Uuid::new_v4())
                    )
                    .col(ColumnDef::new(Admin::UserId).uuid().not_null())
                    .col(ColumnDef::new(Admin::Password).string().not_null())
                    .col(
                        ColumnDef::new(Admin::CreatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Admin::UpdatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Admin::Deleted)
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
                    .name("fk-user-admin")
                    .from(Admin::Table, Admin::UserId)
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

#[derive(DeriveIden)]
enum Post {
    Table,
    Id,
    Title,
    Text,
}
