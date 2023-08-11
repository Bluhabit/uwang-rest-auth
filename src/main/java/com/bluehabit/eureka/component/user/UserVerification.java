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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_user_verification")
@SQLDelete(
    sql = "UPDATE tb_user_profile SET deleted=true WHERE userVerificationId=?"
)
@Where(
    clause = "deleted = false"
)
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
    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted;

    public UserVerification(String token, UserCredential user, VerificationType type) {
        this.token = token;
        this.user = user;
        this.type = type;
    }

}
