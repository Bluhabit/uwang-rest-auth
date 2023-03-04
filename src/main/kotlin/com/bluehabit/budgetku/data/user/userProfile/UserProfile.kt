/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userProfile

import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.UserStatus.NONACTIVE
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType.DATE
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.Date

@Entity
@Table(name = "tb_user_profile")
data class UserProfile(
    @Id
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userId: String? = null,

    @Column
    var userFullName: String,

    @Temporal(
        DATE
    )
    var userDateOfBirth: LocalDate?=null,

    @Column(unique = true)
    var userPhoneNumber: String?=null,

    @Column
    var userCountryCode: String,

    @Column
    var userProfilePicture:String?=null,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)