/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification.notificationCategory

import com.bluehabit.budgetku.data.notification.notification.Notification
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_notification_category"
)
@SQLDelete(
    sql = "UPDATE tb_notification_category SET deleted=true WHERE categoryId=?"
)
@Where(
    clause = "deleted = false"
)
data class NotificationCategory(
    @Id
    var categoryId: String? = null,

    @Column
    var categoryName: String,

    @Column
    var categoryDescription: String,

    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted: Boolean = false

)
