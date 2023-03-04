/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime


@Entity
@Table(name = "tb_user_verification")
data class UserVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userActivationId: String? = null,

    @Column
    var userActivationToken: String,

    @Column
    var userActivationExpired: OffsetDateTime,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REFRESH],
    )
    var user:User?=null,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)