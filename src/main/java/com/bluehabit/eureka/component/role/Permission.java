/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "tb_permission"
)
@SQLDelete(
        sql = "UPDATE tb_permission SET deleted=true WHERE permission_id=?"
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
        private String permissionId;
        @Column(nullable = false)
        private String permissionName;
        @Column(nullable = false)
        private String permissionType;
        @Column
        private String permissionGroup;
        @Column
        private OffsetDateTime createdAt;
        @Column
        private OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        private boolean deleted;
        public Permission(String permissionId, String permissionName, String permissionType, String permissionGroup, OffsetDateTime createdAt, OffsetDateTime updatedAt, boolean deleted) {
                this.permissionId = permissionId;
                this.permissionName = permissionName;
                this.permissionType = permissionType;
                this.permissionGroup = permissionGroup;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
                this.deleted = deleted;
        }

}
