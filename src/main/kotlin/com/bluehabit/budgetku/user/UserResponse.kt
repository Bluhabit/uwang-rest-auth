package com.bluehabit.budgetku.user

import java.time.OffsetDateTime


data class UserResponse(
    var userId: String?=null,
    var userFullName:String,
    var userCountryCode:String,
    var userDateOfBirth:String,
    var userEmail: String,
    var userLevel: LevelUser,
    var userAuthProvider: UserAuthProvider,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)

fun User.toResponse()= UserResponse(
    userId = userId,
    userFullName = userFullName,
    userEmail = userEmail,
    userAuthProvider = userAuthProvider,
    userCountryCode = userCountryCode,
    userDateOfBirth=userDateOfBirth.toString(),
    userLevel = userLevel,
    createdAt = createdAt,
    updatedAt = updatedAt
)