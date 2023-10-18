/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.component.data;

import com.bluehabit.blu.component.NotificationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_notification")
@SQLDelete(
    sql = "UPDATE tb_notification SET deleted=true WHERE id=?"
)
@Where(
    clause = "deleted = false"
)
public class Notification {
    @Id
    @GeneratedValue(
        strategy = GenerationType.UUID
    )
    private String id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private UserCredential user;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private Task task;
    @Column
    private String title;
    @Column
    private String body;
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int2")
    private NotificationType notificationType;
    @Column
    @CreatedDate
    private OffsetDateTime createdAt;
    @Column
    @LastModifiedDate
    private OffsetDateTime updatedAt;
    @Column(
        name = "deleted",
        nullable = false
    )
    private boolean deleted;

}
