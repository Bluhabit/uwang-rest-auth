package com.bluehabit.blu.component.data.Device;

import com.bluehabit.blu.component.data.useCredential.UserCredential;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Entity
@Table(name = "tb_device")
@SQLDelete(
        sql = "UPDATE tb_device SET deleted=true WHERE id=?"
)
@Where(
        clause = "deleted = false"
)
public class Device {
    @Id
    @GenericGenerator(
            name = "UUID",
            type = org.hibernate.id.uuid.UuidGenerator.class
    )
    private String id;
    @ManyToOne
    private UserCredential user;
    @Column
    private String deviceId;
    @Column
    private String deviceName;
    @Column
    private String deviceOS;
    @Column
    private OffsetDateTime createdAt;
    @Column
    private OffsetDateTime updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}