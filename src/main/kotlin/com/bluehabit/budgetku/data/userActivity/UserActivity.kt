/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.userActivity

import com.bluehabit.budgetku.data.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_user_activity"
)
data class UserActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userActivityId: String? = null,
    @Column
    var userActivityDescription: String? = null,
    @Column
    var userActivityType: String? = null,
    @Column
    var userActivityRef: String? = null,
    @ManyToOne(
        cascade = [CascadeType.REFRESH],
        fetch = LAZY
    )
    var user: User? = null,
    @Column
    var createdAt: OffsetDateTime,
    @Column
    var updatedAt: OffsetDateTime

)
