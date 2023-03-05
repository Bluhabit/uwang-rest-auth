/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userCredential

import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.UserStatus.NONACTIVE
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime

@Entity
@Table(name = "tb_user_credential")
data class UserCredential(
    @Id
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userId: String? = null,

    @Column(unique = true)
    var userEmail: String,

    @Column
    var userPassword: String,

    @Column
    var userAuthProvider: String = BASIC.name,

    @Column
    var userStatus: String = NONACTIVE.name,

    @Column
    var userAuthTokenProvider:String,

    @Column
    var userNotificationToken:String,

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = [ALL]
    )
    var userPermissions: Collection<Permission> = listOf(),

    @OneToOne(
        fetch = LAZY,
        cascade = [ALL]
    )
    var userProfile: UserProfile?=null,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)