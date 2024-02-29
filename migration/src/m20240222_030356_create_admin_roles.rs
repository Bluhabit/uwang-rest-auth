use sea_orm_migration::prelude::*;

use crate::entity::*;

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts


        manager
            .create_table(
                Table::create()
                    .table(AdminRole::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(AdminRole::Id)
                            .uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(ColumnDef::new(AdminRole::AdminId).uuid())
                    .col(ColumnDef::new(AdminRole::AccessId).uuid())
                    .col(
                        ColumnDef::new(AdminRole::CreatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(AdminRole::UpdatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(AdminRole::Deleted)
                            .boolean()
                            .not_null()
                            .default(false),
                    )
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
                            .uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(ColumnDef::new(SystemAccess::Name).string().not_null())
                    .col(ColumnDef::new(SystemAccess::Permission).string().not_null())
                    .col(ColumnDef::new(SystemAccess::Group).string().not_null())
                    .col(
                        ColumnDef::new(SystemAccess::CreatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(SystemAccess::UpdatedAt)
                            .timestamp()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(SystemAccess::Deleted)
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
                    .name("fk-admin-role")
                    .from(AdminRole::Table, AdminRole::AdminId)
                    .to(UserCredential::Table, UserCredential::Id)
                    .to_owned(),
            )
            .await?;

        manager
            .create_foreign_key(
                ForeignKey::create()
                    .name("fk-admin-permission")
                    .from(AdminRole::Table, AdminRole::AccessId)
                    .to(SystemAccess::Table, SystemAccess::Id)
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