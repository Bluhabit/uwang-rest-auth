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
                    .as_enum(UserStatus::Table)
                    .values(UserStatus::iter().skip(1))
                    .to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(UserGender::Table)
                    .values(UserGender::iter().skip(1))
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
                    .as_enum(PostType::Table)
                    .values(PostType::iter().skip(1)).to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(CommentType::Table)
                    .values(CommentType::iter().skip(1)).to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(AttachmentType::Table)
                    .values(AttachmentType::iter().skip(1)).to_owned()
            )
            .await?;
        manager
            .create_type(
                Type::create()
                    .as_enum(ReportStatus::Table)
                    .values(ReportStatus::iter().skip(1)).to_owned()
            )
            .await?;
        manager
            .create_type(
                Type::create()
                    .as_enum(ReportType::Table)
                    .values(ReportType::iter().skip(1)).to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(GroupRole::Table)
                    .values(GroupRole::iter().skip(1)).to_owned()
            )
            .await?;

        manager
            .create_type(
                Type::create()
                    .as_enum(NotificationType::Table)
                    .values(NotificationType::iter().skip(1)).to_owned()
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
                            .primary_key()
                            .not_null(),
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
                    .col(ColumnDef::new(UserCredential::DateOfBirth)
                        .date()
                        .null()
                    )
                    .col(ColumnDef::new(UserCredential::Gender)
                        .enumeration(UserGender::Table, UserGender::iter().skip(1))
                        .null())
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
                    .col(ColumnDef::new(UserCredential::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(UserProfile::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(UserProfile::Id).string().primary_key().not_null())
                    .col(ColumnDef::new(UserProfile::Key).string().not_null())
                    .col(ColumnDef::new(UserProfile::Value).string().not_null())
                    .col(ColumnDef::new(UserProfile::UserId).string())
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
                    .col(ColumnDef::new(UserProfile::Deleted).boolean().not_null().default(false))
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
                    .col(ColumnDef::new(Admin::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Admin::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
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
                            .not_null()
                            .default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(SystemAccess::UpdatedAt)
                            .timestamp()
                            .not_null().default(Expr::current_timestamp()),
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
                    .col(ColumnDef::new(AdminAccess::AccessId).string())
                    .col(
                        ColumnDef::new(AdminAccess::CreatedAt)
                            .timestamp()
                            .not_null().default(Expr::current_timestamp()),
                    )
                    .col(
                        ColumnDef::new(AdminAccess::UpdatedAt)
                            .timestamp()
                            .not_null().default(Expr::current_timestamp()),
                    )
                    .col(ColumnDef::new(AdminAccess::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager.create_table(
            Table::create()
                .table(Hashtag::Table)
                .if_not_exists()
                .col(ColumnDef::new(Hashtag::Table).string())
                .col(ColumnDef::new(Hashtag::Id).string().not_null().primary_key())
                .col(ColumnDef::new(Hashtag::Value).string())
                .col(ColumnDef::new(Hashtag::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                .col(ColumnDef::new(Hashtag::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                .col(ColumnDef::new(Hashtag::Deleted).boolean().not_null().default(false))
                .to_owned()
        ).await?;

        manager.create_table(
            Table::create()
                .table(Post::Table)
                .if_not_exists()
                .col(ColumnDef::new(Post::Id).string().primary_key().not_null())
                .col(ColumnDef::new(Post::CreatedBy).string())
                .col(ColumnDef::new(Post::PostId).string())
                .col(ColumnDef::new(Post::Body).text())
                .col(ColumnDef::new(Post::Location).text())
                .col(ColumnDef::new(Post::LikesCount).big_integer().not_null())
                .col(ColumnDef::new(Post::CommentsCount).big_integer().not_null())
                .col(
                    ColumnDef::new(Post::PostType)
                        .enumeration(PostType::Table, PostType::iter().skip(1))
                        .not_null()
                )
                .col(ColumnDef::new(Post::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                .col(ColumnDef::new(Post::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                .col(ColumnDef::new(Post::Deleted).boolean().not_null().default(false))
                .to_owned()
        ).await?;

        manager
            .create_table(
                Table::create()
                    .table(PostComment::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(PostComment::Id).string().primary_key())
                    .col(ColumnDef::new(PostComment::Body).string())
                    .col(ColumnDef::new(PostComment::UserId).string())
                    .col(ColumnDef::new(PostComment::ReplyTo).string())
                    .col(ColumnDef::new(PostComment::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostComment::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostComment::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(PostMention::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(PostMention::Id).string().primary_key())
                    .col(ColumnDef::new(PostMention::PostId).string())
                    .col(ColumnDef::new(PostMention::UserId).string())
                    .col(ColumnDef::new(PostMention::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostMention::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostMention::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(PostHashtag::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(PostHashtag::Id).string().primary_key())
                    .col(ColumnDef::new(PostHashtag::PostId).string())
                    .col(ColumnDef::new(PostHashtag::HashtagId).string())
                    .col(ColumnDef::new(PostHashtag::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostHashtag::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(PostHashtag::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Group::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Group::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(Group::GroupOwner).string())
                    .col(ColumnDef::new(Group::GroupBanner).string())
                    .col(ColumnDef::new(Group::GroupName).string())
                    .col(ColumnDef::new(Group::GroupDescription).text())
                    .col(ColumnDef::new(Group::GroupMemberCount).big_integer())
                    .col(ColumnDef::new(Group::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Group::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Group::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(GroupMember::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(GroupMember::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(GroupMember::GroupId).string())
                    .col(ColumnDef::new(GroupMember::UserId).string())
                    .col(ColumnDef::new(GroupMember::Role).enumeration(
                        GroupRole::Table,
                        GroupRole::iter().skip(1),
                    ))
                    .col(ColumnDef::new(GroupMember::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(GroupMember::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(GroupMember::Deleted).boolean().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Threads::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Threads::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(Threads::ThreadId).string())
                    .col(ColumnDef::new(Threads::GroupId).string())
                    .col(ColumnDef::new(Threads::Title).string())
                    .col(ColumnDef::new(Threads::Description).text())
                    .col(ColumnDef::new(Threads::Upvote).big_integer().not_null().default(0))
                    .col(ColumnDef::new(Threads::DownVote).big_integer().not_null().default(0))
                    .col(ColumnDef::new(Threads::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Threads::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Threads::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(ThreadsComment::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(ThreadsComment::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(ThreadsComment::ThreadId).string())
                    .col(ColumnDef::new(ThreadsComment::ReplyTo).string())
                    .col(ColumnDef::new(ThreadsComment::Body).text())
                    .col(ColumnDef::new(ThreadsComment::Upvote).big_integer().not_null().default(0))
                    .col(ColumnDef::new(ThreadsComment::DownVote).big_integer().not_null().default(0))
                    .col(ColumnDef::new(ThreadsComment::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(ThreadsComment::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(ThreadsComment::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Attachment::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Attachment::Id).string()
                        .primary_key()
                        .not_null())
                    .col(ColumnDef::new(Attachment::PostId).string())
                    .col(ColumnDef::new(Attachment::AttachmentType)
                        .enumeration(AttachmentType::Table, AttachmentType::iter().skip(1)))
                    .col(ColumnDef::new(Attachment::MimeType).string().not_null())
                    .col(ColumnDef::new(Attachment::Ext).string().not_null())
                    .col(ColumnDef::new(Attachment::Value).string())
                    .col(ColumnDef::new(Attachment::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Attachment::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Attachment::Deleted).boolean().not_null().default(false))
                    .to_owned()
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Report::Table)
                    .if_not_exists()
                    .col(
                        ColumnDef::new(Report::Id)
                            .string()
                            .primary_key()
                            .not_null(),
                    )
                    .col(ColumnDef::new(Report::Context).string().not_null())
                    .col(ColumnDef::new(Report::Note).string().not_null())
                    .col(ColumnDef::new(Report::ReferenceId).string())
                    .col(ColumnDef::new(Report::ReportedBy).string())
                    .col(ColumnDef::new(Report::Status)
                        .enumeration(ReportStatus::Table, ReportStatus::iter().skip(1)))
                    .col(ColumnDef::new(Report::ReportType)
                        .enumeration(ReportType::Table, ReportType::iter().skip(1)))
                    .col(ColumnDef::new(Report::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Report::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Report::Deleted).boolean().not_null().default(false))
                    .to_owned(),
            )
            .await?;

        manager
            .create_table(
                Table::create()
                    .table(Notification::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Notification::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(Notification::UserId).string())
                    .col(ColumnDef::new(Notification::Title).string())
                    .col(ColumnDef::new(Notification::Body).text())
                    .col(ColumnDef::new(Notification::Assets).string())
                    .col(ColumnDef::new(Notification::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Notification::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Notification::Deleted).boolean().not_null().default(false))
                    .to_owned()
            ).await?;

        manager
            .create_table(
                Table::create()
                    .table(Notification::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(Notification::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(Notification::UserId).string())
                    .col(ColumnDef::new(Notification::Title).string())
                    .col(ColumnDef::new(Notification::Body).text())
                    .col(ColumnDef::new(Notification::NotificationType).enumeration(
                        NotificationType::Table, NotificationType::iter().skip(1),
                    ))
                    .col(ColumnDef::new(Notification::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Notification::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(Notification::Deleted).boolean().not_null().default(false))
                    .to_owned()
            ).await?;

        manager
            .create_table(
                Table::create()
                    .table(UserNotification::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(UserNotification::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(UserNotification::UserId).string())
                    .col(ColumnDef::new(UserNotification::NotificationId).string())
                    .col(ColumnDef::new(UserNotification::Body).string())
                    .col(ColumnDef::new(UserNotification::IsRead).text())
                    .col(ColumnDef::new(UserNotification::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(UserNotification::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(UserNotification::Deleted).boolean().not_null().default(false))
                    .to_owned()
            ).await?;

        manager
            .create_table(
                Table::create()
                    .table(UserPushToken::Table)
                    .if_not_exists()
                    .col(ColumnDef::new(UserPushToken::Id).string().not_null().primary_key())
                    .col(ColumnDef::new(UserPushToken::UserId).string().not_null())
                    .col(ColumnDef::new(UserPushToken::Token).string().not_null())
                    .col(ColumnDef::new(UserPushToken::CreatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(UserPushToken::UpdatedAt).timestamp_with_time_zone().not_null().default(Expr::current_timestamp()))
                    .col(ColumnDef::new(UserPushToken::Deleted).boolean().not_null().default(false))
                    .to_owned()
            ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-profile")
                .from(UserProfile::Table, UserProfile::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-report")
                .from(Report::Table, Report::ReferenceId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-report-by")
                .from(Report::Table, Report::ReportedBy)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-admin")
                .from(Admin::Table, Admin::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-admin-user")
                .from(AdminAccess::Table, AdminAccess::AdminId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-admin-access")
                .from(AdminAccess::Table, AdminAccess::AccessId)
                .to(SystemAccess::Table, SystemAccess::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-created-by")
                .from(Post::Table, Post::CreatedBy)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-attachment")
                .from(Attachment::Table, Attachment::PostId)
                .to(Post::Table, Post::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-repost-from")
                .from(Post::Table, Post::PostId)
                .to(Post::Table, Post::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-comment-reply-to")
                .from(PostComment::Table, PostComment::ReplyTo)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-mention-to")
                .from(PostMention::Table, PostMention::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-post-report")
                .from(Report::Table, Report::ReferenceId)
                .to(Post::Table, Post::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-hashtag-post")
                .from(PostHashtag::Table, PostHashtag::PostId)
                .to(Post::Table, Post::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-hashtag-id")
                .from(PostHashtag::Table, PostHashtag::HashtagId)
                .to(Hashtag::Table, Hashtag::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-group-owner")
                .from(Group::Table, Group::GroupOwner)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-group-member")
                .from(GroupMember::Table, GroupMember::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-group-member-of")
                .from(GroupMember::Table, GroupMember::GroupId)
                .to(Group::Table, Group::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-thread-repost-from")
                .from(Threads::Table, Threads::ThreadId)
                .to(Threads::Table, Threads::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-thread-comment-attachment")
                .from(Attachment::Table, Attachment::Id)
                .to(ThreadsComment::Table, ThreadsComment::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-notification-user")
                .from(Notification::Table, Notification::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-notification-for")
                .from(UserNotification::Table, UserNotification::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-notification-from")
                .from(UserNotification::Table, UserNotification::NotificationId)
                .to(Notification::Table, Notification::Id)
                .to_owned()
        ).await?;

        manager.create_foreign_key(
            ForeignKey::create()
                .name("fk-user-push-token")
                .from(UserPushToken::Table, UserPushToken::UserId)
                .to(UserCredential::Table, UserCredential::Id)
                .to_owned()
        ).await?;

        manager.create_index(
            Index::create()
                .name("index-admin-email")
                .table(UserCredential::Table)
                .col(UserCredential::Email)
                .to_owned()
        ).await?;

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
    Username,
    Password,
    AuthProvider,
    Status,
    FullName,
    DateOfBirth,
    Gender,
    CreatedAt,
    UpdatedAt,
    Deleted,
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
enum Report {
    Table,
    Id,
    ReferenceId,
    ReportedBy,
    Context,
    Note,
    Status,
    ReportType,
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
enum Post {
    Table,
    Id,
    CreatedBy,
    PostId,
    Location,
    Body,
    PostType,
    LikesCount,
    CommentsCount,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum PostMention {
    Table,
    Id,
    UserId,
    PostId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum PostHashtag {
    Table,
    Id,
    PostId,
    HashtagId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Hashtag {
    Table,
    Id,
    Value,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Attachment {
    Table,
    Id,
    PostId,
    AttachmentType,
    MimeType,
    Ext,
    Value,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum PostComment {
    Table,
    Id,
    Body,
    ReplyTo,
    UserId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Group {
    Table,
    Id,
    GroupName,
    GroupDescription,
    GroupOwner,
    GroupBanner,
    GroupMemberCount,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum GroupMember {
    Table,
    Id,
    UserId,
    GroupId,
    Role,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Threads {
    Table,
    Id,
    ThreadId,
    GroupId,
    Title,
    Description,
    Upvote,
    DownVote,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum ThreadsComment {
    Table,
    Id,
    ReplyTo,
    ThreadId,
    Body,
    Upvote,
    DownVote,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum UserPushToken {
    Table,
    Id,
    UserId,
    Token,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum Notification {
    Table,
    Id,
    UserId,
    Title,
    Body,
    Assets,
    NotificationType,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
enum UserNotification {
    Table,
    Id,
    UserId,
    NotificationId,
    Body,
    IsRead,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(Iden, EnumIter)]
pub enum UserStatus {
    Table,
    #[iden = "ACTIVE"]
    ACTIVE,
    #[iden = "INACTIVE"]
    LOCKED,
    #[iden = "LOCKED"]
    INACTIVE,
    #[iden = "SUSPENDED"]
    SUSPENDED,
    #[iden = "WAITING_CONFIRMATION"]
    WAITING,
}

#[derive(Iden, EnumIter)]
pub enum UserGender {
    Table,
    #[iden = "MALE"]
    MALE,
    #[iden = "FEMALE"]
    FEMALE,
}

#[derive(Iden, EnumIter)]
pub enum PostType {
    Table,
    #[iden = "BASIC"]
    BASIC,
    #[iden = "POLLING"]
    POLLING,
}

#[derive(Iden, EnumIter)]
pub enum AttachmentType {
    Table,
    #[iden = "IMAGE"]
    IMAGE,
    #[iden = "VIDEO"]
    VIDEO,
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

#[derive(Iden, EnumIter)]
pub enum CommentType {
    Table,
    #[iden = "REPLY"]
    REPLY,
    #[iden = "COMMENT"]
    COMMENT,
}

#[derive(Iden, EnumIter)]
pub enum ReportStatus {
    Table,
    #[iden = "OPEN"]
    OPEN,
    #[iden = "CLOSED"]
    CLOSED,
    #[iden = "CANCELED"]
    CANCELED,
}

#[derive(Iden, EnumIter)]
pub enum ReportType {
    Table,
    #[iden = "POST"]
    POST,
    #[iden = "USER"]
    USER,
}

#[derive(Iden, EnumIter)]
pub enum GroupRole {
    Table,
    #[iden = "ADMIN"]
    ADMIN,
    #[iden = "MODERATOR"]
    MODERATOR,
    #[iden = "USER"]
    USER,
}

#[derive(Iden, EnumIter)]
pub enum NotificationType {
    Table,
    #[iden = "NEW_FOLLOWER"]
    NewFollower,
}

