/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.data;

import com.bluehabit.eureka.component.AuthProvider;
import com.bluehabit.eureka.component.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
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
    private String id;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int2")
    private AuthProvider authProvider;
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int2")
    private UserStatus status;
    @OneToMany
    private List<UserProfile> userInfo;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    public Map<String, Object> toResponse() {
        final Map<String, Object> data = new HashMap<>();
        final Map<String, Object> profile = new HashMap<>();
        getUserInfo().forEach(userProfile -> profile.put(userProfile.getKey(), userProfile.getValue()));
        data.put("id", getId());
        data.put("email", getEmail());
        data.put("status", getStatus());
        data.put("userInfo", profile);
        data.put("createdAt", getCreatedAt());
        data.put("updatedAt", getUpdatedAt());

        return data;
    }
}
