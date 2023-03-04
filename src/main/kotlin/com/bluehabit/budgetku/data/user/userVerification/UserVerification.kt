/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userVerification

import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import jakarta.persistence.CascadeType.ALL
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
    var userId:String,

    @Column
    var userActivationToken: String,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var activationAt: OffsetDateTime?=null
)