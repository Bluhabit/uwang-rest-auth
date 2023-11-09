package com.bluehabit.blu.component.data.admin;

import com.bluehabit.blu.component.data.adminPermission.AdminPermission;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "tb_admin")
@SQLDelete(
        sql = "UPDATE tb_admin SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class Admin {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @OneToOne
    private UserCredential user;
    @ManyToMany
    private List<AdminPermission> permissions;
    @Column
    private String reason;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
