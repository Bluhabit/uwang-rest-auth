package com.bluehabit.blu.component.data.adminPermission;

import com.bluehabit.blu.component.data.useCredential.UserCredential;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Entity
@Table(name = "tb_admin_permission")
@SQLDelete(
        sql = "UPDATE tb_admin SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class AdminPermission {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @Column
    private String permission;
    @Column
    private String name;
    @Column
    private String group;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}