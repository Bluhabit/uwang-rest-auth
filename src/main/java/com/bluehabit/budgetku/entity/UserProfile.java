package com.bluehabit.budgetku.entity;

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

@Entity
@Table(name = "tb_user_profile")
@SQLDelete(
        sql = "UPDATE tb_user_profile SET deleted=true WHERE id=?"
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
        String userId;
        @Column
        String userFullName;
        @Temporal(TemporalType.DATE)
        LocalDate userDateOfBirth;
        @Column
        String userPhoneUmber;
        @Column
        String userCountryCode;
        @Column
        String userProfilePicture;
        @Column
        OffsetDateTime createdAt;
        @Column
        OffsetDateTime updatedAt;
        @Column(
                name = "deleted",
                nullable = false
        )
        boolean deleted;

        public UserProfile(){}


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

        public String getUserId() {
                return userId;
        }

        public void setUserId(String userId) {
                this.userId = userId;
        }

        public String getUserFullName() {
                return userFullName;
        }

        public void setUserFullName(String userFullName) {
                this.userFullName = userFullName;
        }

        public LocalDate getUserDateOfBirth() {
                return userDateOfBirth;
        }

        public void setUserDateOfBirth(LocalDate userDateOfBirth) {
                this.userDateOfBirth = userDateOfBirth;
        }

        public String getUserPhoneUmber() {
                return userPhoneUmber;
        }

        public void setUserPhoneUmber(String userPhoneUmber) {
                this.userPhoneUmber = userPhoneUmber;
        }

        public String getUserCountryCode() {
                return userCountryCode;
        }

        public void setUserCountryCode(String userCountryCode) {
                this.userCountryCode = userCountryCode;
        }

        public String getUserProfilePicture() {
                return userProfilePicture;
        }

        public void setUserProfilePicture(String userProfilePicture) {
                this.userProfilePicture = userProfilePicture;
        }

        public OffsetDateTime getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(OffsetDateTime createdAt) {
                this.createdAt = createdAt;
        }

        public OffsetDateTime getUpdatedAt() {
                return updatedAt;
        }

        public void setUpdatedAt(OffsetDateTime updatedAt) {
                this.updatedAt = updatedAt;
        }

        public boolean isDeleted() {
                return deleted;
        }

        public void setDeleted(boolean deleted) {
                this.deleted = deleted;
        }
}
