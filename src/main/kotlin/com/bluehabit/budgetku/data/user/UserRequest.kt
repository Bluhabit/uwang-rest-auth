/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class SignInWithEmailRequest(
    @field:NotBlank
    var email:String?,
    @field:NotBlank
    var password:String?
)

data class SignInWithGoogleRequest(
    @field:NotBlank
    var token:String?
)

data class SignUpWithEmailRequest(
    @field:NotBlank
    var fullName:String?,
    @field:NotBlank
    var email:String?,
    @field:NotBlank
    var password:String?
)
data class SignUpWithGoogleRequest(
    @field:NotBlank
    var token:String?
)

data class AssignPermissionRequest(
    @field:NotBlank
    var userId:String?,
    @field:NotEmpty
    var permissions:List<String>?
)

data class BannedUserRequest(
    @field:NotBlank
    var userId: String?,
    @field:NotBlank
    var reason:String?
)
data class UpdateProfilePictureRequest(
    @field:NotNull
    var file:MultipartFile?
)
