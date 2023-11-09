package com.bluehabit.blu.component.data.userReport;

import com.bluehabit.blu.component.data.useCredential.UserCredential;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Entity
@Table(name = "tb_user_report")
@SQLDelete(
        sql = "UPDATE tb_user_report SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class UserReport {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @ManyToOne
    private UserCredential user;
    @ManyToOne
    private UserCredential reportBy;
    @Column
    private String reason;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
