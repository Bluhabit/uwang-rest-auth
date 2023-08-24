/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Table(
    name = "tb_user_firebase_token"
)
@SQLDelete(
    sql = "UPDATE tb_user_firebase_token SET deleted=true WHERE id=?"
)
@Where(
    clause = "deleted = false"
)
public class UserFirebaseToken {
    @Id
    @GenericGenerator(
        name = "UUID",
        type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private UserCredential user;
    @Column
    private String token;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted;
}
