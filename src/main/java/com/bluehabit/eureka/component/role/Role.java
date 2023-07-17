/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "tb_role"
)
@SQLDelete(
        sql = "UPDATE tb_role SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class Role {
        @Id
        @GenericGenerator(
                name = "UUID",
                type = org.hibernate.id.uuid.UuidGenerator.class
        )
        private String id;
        @Column
        private String roleName;
        @Column
        private String roleDescription;
        @ManyToMany(
                fetch = FetchType.EAGER,
                cascade = {CascadeType.ALL}
        )
        private Collection<Permission> rolePermission;
        @Column
        private OffsetDateTime createdAt;
        @Column
        private OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        private boolean deleted;

        public Role(String id, String roleName, String roleDescription, Collection<Permission> rolePermission, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
                this.id = id;
                this.roleName = roleName;
                this.roleDescription = roleDescription;
                this.rolePermission = rolePermission;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:m:s");
                return createdAt.format(formatter);
        }
        public String getUpdatedAt() {
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy h:m:s");
                return updatedAt.format(formatter);
        }

}
