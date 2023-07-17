/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.role.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Entity
@Table(
        name = "tb_permission_group"
)
@SQLDelete(
        sql = "UPDATE tb_permission_group SET deleted=true WHERE role_id=?"
)
@Where(
        clause = "deleted = false"
)
public class PermissionGroup {
        @Id
        @GenericGenerator(
                name = "UUID",
                type = org.hibernate.id.uuid.UuidGenerator.class
        )
        String roleId;
        @Column
        String roleName;
        @Column
        String roleDescription;
        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = {CascadeType.ALL}
        )
        Collection<Permission> rolePermission;
        @Column
        OffsetDateTime createdAt;
        @Column
        OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        boolean deleted;
        public PermissionGroup(){}

        public PermissionGroup(String roleId, String roleName, String roleDescription, Collection<Permission> rolePermission, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
                this.roleId = roleId;
                this.roleName = roleName;
                this.roleDescription = roleDescription;
                this.rolePermission = rolePermission;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }

        public String getRoleId() {
                return roleId;
        }

        public void setRoleId(String roleId) {
                this.roleId = roleId;
        }

        public String getRoleName() {
                return roleName;
        }

        public void setRoleName(String roleName) {
                this.roleName = roleName;
        }

        public String getRoleDescription() {
                return roleDescription;
        }

        public void setRoleDescription(String roleDescription) {
                this.roleDescription = roleDescription;
        }

        public Collection<Permission> getRolePermission() {
                return rolePermission;
        }

        public void setRolePermission(Collection<Permission> rolePermission) {
                this.rolePermission = rolePermission;
        }

        public String getCreatedAt() {
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:m:s");
                return createdAt.format(formatter);
        }

        public void setCreatedAt(OffsetDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:m:s");
                return updatedAt.format(formatter);
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
