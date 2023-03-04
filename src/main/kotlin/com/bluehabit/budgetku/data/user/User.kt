/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.data.permission.Permission
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime

@Entity
@Table(name = "tb_user")
data class User(
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userId: String? = null,

    @Column
    var userFullName: String,

    @Column
    var userDateOfBirth: OffsetDateTime,

    @Column(unique = true)
    var userEmail: String,

    @Column(unique = true)
    var userPhoneNumber: String,

    @Column
    var userCountryCode: String,

    @Column
    var userPassword: String,

    @Column
    var userProfilePicture:String,

    @Column
    var userAuthProvider: String = UserAuthProvider.BASIC.name,

    @Column
    var userStatus: String = UserStatus.NONACTIVE.name,

    @Column
    var userAuthTokenProvider:String,

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.ALL]
    )
    @JsonIgnore
    var userPermissions: Collection<Permission> = listOf(),

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)