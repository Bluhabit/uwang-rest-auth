/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

import com.bluehabit.eureka.component.role.Permission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_user_credential")
@SQLDelete(
        sql = "UPDATE tb_user_credential SET deleted=true WHERE user_id=?"
)
@Where(
        clause = "deleted = false"
)
public class UserCredential {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String userId;
    @Column(unique = true)
    private String userEmail;
    @Column
    private String userPassword;
    @Column
    private String userStatus;
    @Column
    private String userAuthProvider;
    @Column
    private String userNotificationToken;
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    private Collection<Permission> userPermission;
    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    private UserProfile userProfile;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(
            name = "deleted",
            nullable = false
    )
    private boolean deleted;

    public UserCredential(String userId, String userEmail, String userPassword, String userStatus, String userAuthProvider, String userNotificationToken, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userStatus = userStatus;
        this.userAuthProvider = userAuthProvider;
        this.userNotificationToken = userNotificationToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
