/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.permission.PermissionReponse
import com.bluehabit.budgetku.data.permission.toResponse
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import org.springframework.data.domain.Page
import java.time.OffsetDateTime


data class UserResponse(
    var userId: String? = null,
    var userEmail: String,
    var userAuthProvider: String,
    var userStatus: String,
    var userPermission: List<PermissionReponse>,
    var userProfile: UserProfileResponse?,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)

data class UserProfileResponse(
    var userId: String? = null,
    var userFullName: String,
    var userCountryCode: String,
    var userDateOfBirth: String,
    var userProfilePicture:String,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)


fun UserCredential.toResponse(): UserResponse {

    return UserResponse(
        userId = userId,
        userEmail = userEmail,
        userAuthProvider = userAuthProvider,
        userStatus = userStatus,
        userPermission = userPermissions.map { it.toResponse() },
        userProfile = userProfile?.toResponse(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UserProfile.toResponse(): UserProfileResponse {

    return UserProfileResponse(
        userId = userId,
        userFullName = userFullName,
        userCountryCode = userCountryCode,
        userDateOfBirth = userDateOfBirth.toString(),
        userProfilePicture = userProfilePicture.orEmpty(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

@JvmName("userCredentialPagingToResponse")
fun Page<UserCredential>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}

@JvmName("userProfilePagingToResponse")
fun Page<UserProfile>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}