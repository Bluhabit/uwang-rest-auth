package com.bluehabit.budgetku.data.user

import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LoginRequest(
    @field:NotBlank
    var email:String?,
    @field:NotBlank
    var password:String?
)

data class CreateNewUserRequest(
    @field:NotBlank
    var userCountryCode:String?,
    @field:NotBlank
    var userEmail:String?,
    @field:NotBlank
    var userPassword:String?,
    @field:NotBlank
    var userFullName:String?,
    @field:NotNull
    var userLevel: UserStatus?,
    @field:NotBlank
    var userPhoneNumber:String?,
    @field:NotNull
    var userAuthProvider: UserAuthProvider?,
)

data class ResetPasswordRequest(
    @field:NotNull
    var userId:String,
    @field:NotBlank
    var currentPassword:String,
    @field:NotBlank
    var newPassword:String
)
fun CreateNewUserRequest.toEntity() = User(
    userId = null,
    userEmail = userEmail!!,
    userPassword = userPassword!!,
    userFullName=userFullName!!,
    userAuthProvider= userAuthProvider!!,
    userAuthTokenProvider="",
    userDateOfBirth=OffsetDateTime.now(),
    userCountryCode=userCountryCode!!,
    userPhoneNumber=userPhoneNumber!!,
    createdAt = OffsetDateTime.now(),
    updatedAt = OffsetDateTime.now()
)
