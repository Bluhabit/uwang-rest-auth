package com.bluehabit.budgetku.admin.auth

import com.bluehabit.budgetku.model.LevelUser
import java.time.OffsetDateTime


data class UserResponse(
    var id: Long?=null,
    var email: String,
    var levelUser: LevelUser,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)

fun User.toResponse()= UserResponse(
    id, email, levelUser, createdAt, updatedAt
)