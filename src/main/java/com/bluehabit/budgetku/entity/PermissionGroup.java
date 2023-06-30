package com.bluehabit.budgetku.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.Collection;

@Entity
@Table(
        name = "tb_permission_group"
)
@SQLDelete(
        sql = "UPDATE tb_permission_group SET deleted=true WHERE permissionId=?"
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
