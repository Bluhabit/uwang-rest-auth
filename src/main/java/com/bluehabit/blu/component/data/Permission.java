/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.component.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "tb_permission"
)
@SQLDelete(
    sql = "UPDATE tb_permission SET deleted=true WHERE id=?"
)
@Where(
    clause = "deleted = false"
)
public class Permission {
    @Id
    @GenericGenerator(
        name = "UUID",
        type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @Column(nullable = false)
    private String permission;
    @Column(nullable = false)
    private String name;
    @Column
    private String permissionGroup;
    @Column
    @CreatedDate
    private OffsetDateTime createdAt;
    @Column
    @LastModifiedDate
    private OffsetDateTime updatedAt;
    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted;
}
