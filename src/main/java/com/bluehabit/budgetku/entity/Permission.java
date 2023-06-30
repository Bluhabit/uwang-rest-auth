package com.bluehabit.budgetku.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "tb_permission"
)
@SQLDelete(
        sql = "UPDATE tb_permission SET deleted=true WHERE permissionId=?"
)
@Where(
        clause = "deleted = false"
)
public class Permission{
        @Id
        @GenericGenerator(
                name = "UUID",
                type = org.hibernate.id.uuid.UuidGenerator.class
        )
        String permissionId;
        @Column(nullable = false)
        String permissionName;
        @Column(nullable = false)
        String permissionType;
        @Column
        String permissionGroup;
        @Column
        OffsetDateTime createdAt;
        @Column
        OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        boolean deleted;

        public Permission(){}
        public Permission(String permissionId, String permissionName, String permissionType, String permissionGroup, OffsetDateTime createdAt, OffsetDateTime updatedAt, boolean deleted) {
                this.permissionId = permissionId;
                this.permissionName = permissionName;
                this.permissionType = permissionType;
                this.permissionGroup = permissionGroup;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
                this.deleted = deleted;
        }

        public String getPermissionId() {
                return permissionId;
        }

        public void setPermissionId(String permissionId) {
                this.permissionId = permissionId;
        }

        public String getPermissionName() {
                return permissionName;
        }

        public void setPermissionName(String permissionName) {
                this.permissionName = permissionName;
        }

        public String getPermissionType() {
                return permissionType;
        }

        public void setPermissionType(String permissionType) {
                this.permissionType = permissionType;
        }

        public String getPermissionGroup() {
                return permissionGroup;
        }

        public void setPermissionGroup(String permissionGroup) {
                this.permissionGroup = permissionGroup;
        }

        public OffsetDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(OffsetDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public OffsetDateTime getUpdatedAt() {
                return updatedAt;
        }

        public void setUpdatedAt(OffsetDateTime updatedAt) {
                this.updatedAt = updatedAt;
        }

        public boolean isDeleted() {
                return deleted;
        }

        public void setDeleted(boolean deleted) {
                this.deleted = deleted;
        }
}
