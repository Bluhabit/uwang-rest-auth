use sea_orm_migration::prelude::*;
use sea_orm_migration::sea_orm::Iterable;
use crate::extension::postgres::Type;

use crate::entity::{*};

#[derive(DeriveMigrationName)]
pub struct Migration;

#[async_trait::async_trait]
impl MigrationTrait for Migration {
    async fn up(&self, manager: &SchemaManager) -> Result<(), DbErr> {
        // Replace the sample below with your own migration scripts
        manager
            .create_type(
                Type::create()
                    .as_enum(PostType::Table)
                    .values(PostType::iter().skip(1))
                    .to_owned(),
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(AttachmentType::Table)
                    .values(AttachmentType::iter().skip(1))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Post::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Post::Id)
                        .uuid()
                        .primary_key()
                        .not_null()
                        .default(uuid::Uuid::new_v4()))
                    .col(ColumnDef::new(Post::CreatedBy).uuid())
                    .col(ColumnDef::new(Post::PostId).uuid())
                    .col(ColumnDef::new(Post::Body).text())
                    .col(ColumnDef::new(Post::Location).text())
                    .col(ColumnDef::new(Post::LikesCount).big_integer().not_null())
                    .col(ColumnDef::new(Post::CommentsCount).big_integer().not_null())
                    .col(
                        ColumnDef::new(Post::PostType)
                            .enumeration(PostType::Table, PostType::iter().skip(1))
                            .not_null(),
                    )
                    .col(
                        ColumnDef::new(Post::CreatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Post::UpdatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Post::Deleted)
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
                    .table(Attachment::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(Attachment::Id).uuid()
                            .primary_key()
                            .not_null()
                            .default(uuid::Uuid::new_v4()),
                    )
                    .col(ColumnDef::new(Attachment::PostId).uuid())
                    .col(
                        ColumnDef::new(Attachment::AttachmentType)
                            .enumeration(AttachmentType::Table, AttachmentType::iter().skip(1)),
                    )
                    .col(ColumnDef::new(Attachment::MimeType).string().not_null())
                    .col(ColumnDef::new(Attachment::Ext).string().not_null())
                    .col(ColumnDef::new(Attachment::Value).string())
                    .col(
                        ColumnDef::new(Attachment::CreatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Attachment::UpdatedAt)
                            .timestamp_with_time_zone()
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(Attachment::Deleted)
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
                    .name("fk-post-created-by")
                    .from(Post::Table, Post::CreatedBy)
                    .to(UserCredential::Table, UserCredential::Id)
                    .to_owned(),
            )
            .await?;

        manager
            .create_foreign_key(
                ForeignKey::create()
                    .name("fk-post-attachment")
                    .from(Attachment::Table, Attachment::PostId)
                    .to(Post::Table, Post::Id)
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
