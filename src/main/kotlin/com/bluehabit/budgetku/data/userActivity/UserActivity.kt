/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.userActivity

import com.bluehabit.budgetku.data.user.User
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

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
        cascade = [REFRESH],
        fetch = LAZY
    )
    var user: User? = null,
    @Column
    var createdAt: OffsetDateTime,
    @Column
    var updatedAt: OffsetDateTime

)
