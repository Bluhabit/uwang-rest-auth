package com.bluehabit.blu.component.data.userLogin;

import com.bluehabit.blu.component.data.Device.Device;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Entity
@Table(name = "tb_user_login")
@SQLDelete(
        sql = "UPDATE tb_user_login SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class UserLogin {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @ManyToOne
    private UserCredential user;
    @ManyToOne
    private Device deviceId;
    @Column
    private String token;
    @Column
    private OffsetDateTime loginAt;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
