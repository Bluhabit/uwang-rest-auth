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
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_notification_read"
)
@SQLDelete(
    sql = "UPDATE tb_notification_read SET deleted=true WHERE readId=?"
)
@Where(
    clause = "deleted = false"
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
    var updatedAt: OffsetDateTime? = null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted:Boolean = false
)
