pub use sea_orm_migration::prelude::*;

mod m20240222_024732_create_user_credential;
mod m20240222_025325_create_user_profile;
mod m20240222_025650_create_user_logs;
mod m20240222_025842_create_admin;
mod m20240222_030356_create_admin_roles;
mod m20240222_030858_create_post;

mod entity;

pub struct Migrator;

#[async_trait::async_trait]
impl MigratorTrait for Migrator {
    fn migrations() -> Vec<Box<dyn MigrationTrait>> {
        vec![
            Box::new(m20240222_024732_create_user_credential::Migration),
            Box::new(m20240222_025325_create_user_profile::Migration),
            Box::new(m20240222_025650_create_user_logs::Migration),
            Box::new(m20240222_025842_create_admin::Migration),
            Box::new(m20240222_030356_create_admin_roles::Migration),
            Box::new(m20240222_030858_create_post::Migration),
        ]
    }
}
