/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component;

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

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_user")
@SQLDelete(
        sql = "UPDATE tb_user SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class User {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @Column(unique = true)
    private String userEmail;
    @Column
    private String userPassword;
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(
            name = "deleted",
            nullable = false
    )
    private boolean deleted;
}
