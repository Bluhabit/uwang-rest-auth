/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification.notification

import com.bluehabit.budgetku.data.notification.notificationCategory.NotificationCategory
import com.bluehabit.budgetku.data.notification.notificationRead.NotificationRead
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_notification"
)
@SQLDelete(
    sql = "UPDATE tb_notification SET deleted=true WHERE notificationId=?"
)
@Where(
    clause = "deleted = false"
)
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var notificationId: String?=null,
    @Column
    var notificationTitle: String,
    @Column
    var notificationDescription: String,
    @Column
    var notificationBody: String,

    @ManyToOne(
        fetch = LAZY,
        cascade = [CascadeType.ALL]
    )
    var user: UserProfile? = null,

    @ManyToOne(
        fetch = LAZY,
        cascade = [CascadeType.ALL]
    )
    var notificationCategory:NotificationCategory? =null,

    @ManyToMany(
        fetch = EAGER,
        cascade = [CascadeType.ALL]
    )
    var notificationRead: Collection<NotificationRead> = listOf(),
    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted:Boolean = false
)
