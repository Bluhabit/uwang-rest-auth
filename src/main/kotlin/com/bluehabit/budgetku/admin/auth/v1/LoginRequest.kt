package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.common.model.LevelUser
import java.time.OffsetDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class LoginRequest(
    @field:NotBlank
    var email:String?,
    @field:NotBlank
    var password:String?
)

data class UserRequest(
    @field:NotBlank
    var email:String?,
    @field:NotBlank
    var password:String?,
    @field:NotNull
    var levelUser: LevelUser?
)

data class ResetPasswordRequest(
    @field:NotNull
    var userId:Long,
    @field:NotBlank
    var currentPassword:String,
    @field:NotBlank
    var newPassword:String
)
fun UserRequest.toEntity() = User(
    id = null,
    email=email!!,
    password = password!!,
    levelUser=levelUser!!,
    createdAt = OffsetDateTime.now(),
    updatedAt = OffsetDateTime.now()
)
