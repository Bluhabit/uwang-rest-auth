/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification.notificationRead

import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime

@Entity(
    name = "tb_notification_read"
)
data class NotificationRead(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var readId: String? = null,

    @Column
    var isRead: Boolean,

    @ManyToOne(
        fetch = LAZY,
        cascade = [CascadeType.ALL]
    )
    var userProfile: UserProfile?,

    @ManyToMany(
        fetch = LAZY,
        cascade = [CascadeType.ALL],
    )
    @JsonIgnore
    var notification: Collection<Notification> = listOf(),

    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null
)
