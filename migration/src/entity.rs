use sea_orm_migration::prelude::*;
use sea_orm_migration::sea_orm::EnumIter;

#[derive(DeriveIden)]
pub enum UserCredential {
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
pub enum UserLog {
    Table,
    Id,
    LogType,
    UserId,
    Title,
    Body,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum UserProfile {
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
pub enum Report {
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
pub enum Admin {
    Table,
    Id,
    UserId,
    Password,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum AdminRole {
    Table,
    Id,
    AdminId,
    AccessId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum SystemAccess {
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
pub enum Post {
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
pub enum PostMention {
    Table,
    Id,
    UserId,
    PostId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum PostHashtag {
    Table,
    Id,
    PostId,
    HashtagId,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum Hashtag {
    Table,
    Id,
    Value,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum Attachment {
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
pub enum PostComment {
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
pub enum Group {
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
pub enum GroupMember {
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
pub enum Threads {
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
pub enum ThreadsComment {
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
pub enum UserPushToken {
    Table,
    Id,
    UserId,
    Token,
    CreatedAt,
    UpdatedAt,
    Deleted,
}

#[derive(DeriveIden)]
pub enum Notification {
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
pub enum UserNotification {
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
    #[iden = "HIDDEN"]
    HIDDEN,
    #[iden = "UNKNOWN"]
    UNKNOWN,
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
