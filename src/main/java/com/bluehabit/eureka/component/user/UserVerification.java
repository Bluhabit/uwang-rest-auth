/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

import com.bluehabit.eureka.component.user.verification.VerificationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_user_verification")
public class UserVerification {
    @Id
    @GeneratedValue(
        strategy = GenerationType.UUID
    )
    private String userVerificationId;

    @Column(
        unique = true,
        nullable = false
    )
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private UserCredential user;

    @Column
    private VerificationType type;
    @Column
    @CreatedDate
    private OffsetDateTime createdAt;
    @Column
    @LastModifiedDate
    private OffsetDateTime updatedAt;

    public UserVerification(String token, UserCredential user, VerificationType type) {
        this.token = token;
        this.user = user;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserCredential getUser() {
        return user;
    }

    public void setUser(UserCredential user) {
        this.user = user;
    }

    public VerificationType getType() {
        return type;
    }

    public void setType(VerificationType type) {
        this.type = type;
    }
}
