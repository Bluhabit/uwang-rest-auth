package com.bluehabit.budgetku.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_user_credential")
@SQLDelete(
        sql = "UPDATE tb_user_credential SET deleted=true WHERE id=?"
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
    String userId;
    @Column(unique = true)
    String userEmail;
    @Column
    String userPassword;
    @Column
    String userStatus;
    @Column
    String userAuthProvider;
    @Column
    String userNotificationToken;
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    Collection<Permission> userPermission;
    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL}
    )
    UserProfile userProfile;
    @Column
    OffsetDateTime createdAt;
    @Column
    OffsetDateTime updatedAt;
    @Column(
            name = "deleted",
            nullable = false
    )
    boolean deleted;

    public UserCredential(){}

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserAuthProvider() {
        return userAuthProvider;
    }

    public void setUserAuthProvider(String userAuthProvider) {
        this.userAuthProvider = userAuthProvider;
    }

    public String getUserNotificationToken() {
        return userNotificationToken;
    }

    public void setUserNotificationToken(String userNotificationToken) {
        this.userNotificationToken = userNotificationToken;
    }

    public Collection<Permission> getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(Collection<Permission> userPermission) {
        this.userPermission = userPermission;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
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
