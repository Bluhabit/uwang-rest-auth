/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_user_profile")
@SQLDelete(
        sql = "UPDATE tb_user_profile SET deleted=true WHERE user_id=?"
)
@Where(
        clause = "deleted = false"
)
public class UserProfile{
        @Id
        @GenericGenerator(
                name = "UUID",
                type = org.hibernate.id.uuid.UuidGenerator.class
        )
        private String userId;
        @Column
        private String userFullName;
        @Temporal(TemporalType.DATE)
        private LocalDate userDateOfBirth;
        @Column
        private String userPhoneUmber;
        @Column
        private String userCountryCode;
        @Column
        private String userProfilePicture;
        @Column
        private OffsetDateTime createdAt;
        @Column
        private OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        private boolean deleted;

        public UserProfile(String userId, String userFullName, LocalDate userDateOfBirth, String userPhoneUmber, String userCountryCode, String userProfilePicture, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
                this.userId = userId;
                this.userFullName = userFullName;
                this.userDateOfBirth = userDateOfBirth;
                this.userPhoneUmber = userPhoneUmber;
                this.userCountryCode = userCountryCode;
                this.userProfilePicture = userProfilePicture;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }
}
