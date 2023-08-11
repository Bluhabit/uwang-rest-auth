/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

<<<<<<< HEAD
import com.bluehabit.eureka.component.user.verification.VerificationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
=======
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
>>>>>>> 069a51f6f8029c2560ed375d37cf49720a9bc200
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
<<<<<<< HEAD
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
=======
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
>>>>>>> 069a51f6f8029c2560ed375d37cf49720a9bc200

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_user_verification")
@SQLDelete(
<<<<<<< HEAD
    sql = "UPDATE tb_user_profile SET deleted=true WHERE userVerificationId=?"
=======
    sql = "UPDATE tb_user_profile SET deleted=true WHERE user_id=?"
>>>>>>> 069a51f6f8029c2560ed375d37cf49720a9bc200
)
@Where(
    clause = "deleted = false"
)
public class UserVerification {
    @Id
<<<<<<< HEAD
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
=======
    @GenericGenerator(
        name = "UUID",
        type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @Column
    private String token;
    @Column
    private String type;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserCredential userCredential;

    @Column
    private OffsetDateTime createdAt;
    @Column
>>>>>>> 069a51f6f8029c2560ed375d37cf49720a9bc200
    private OffsetDateTime updatedAt;
    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted;
<<<<<<< HEAD

    public UserVerification(String token, UserCredential user, VerificationType type) {
        this.token = token;
        this.user = user;
        this.type = type;
    }

=======
>>>>>>> 069a51f6f8029c2560ed375d37cf49720a9bc200
}
