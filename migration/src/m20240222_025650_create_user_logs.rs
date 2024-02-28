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
                    .table(UserLog::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(UserLog::Id)
                            .uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(ColumnDef::new(UserLog::UserId).uuid())
                    .col(ColumnDef::new(UserLog::LogType).string())
                    .col(ColumnDef::new(UserLog::Content).string())
                    .col(ColumnDef::new(UserLog::Device).string())
                    .col(ColumnDef::new(UserLog::Activity).string())
                    .col(
                        ColumnDef::new(UserLog::CreatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserLog::UpdatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(UserLog::Deleted)
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
                    .name("fk-user-logs")
                    .from(UserLog::Table, UserLog::UserId)
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
