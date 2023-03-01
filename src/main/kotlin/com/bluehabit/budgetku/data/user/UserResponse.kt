/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.permission.PermissionReponse
import com.bluehabit.budgetku.data.permission.toResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime


data class UserResponse(
    var userId: String? = null,
    var userFullName: String,
    var userCountryCode: String,
    var userDateOfBirth: String,
    var userEmail: String,
    var userAuthProvider: String,
    var userStatus: String,
    var userPermission: List<PermissionReponse>,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)


fun User.toResponse(): UserResponse {

    return UserResponse(
        userId = userId,
        userFullName = userFullName,
        userEmail = userEmail,
        userAuthProvider = userAuthProvider,
        userStatus = userStatus,
        userCountryCode = userCountryCode,
        userDateOfBirth = userDateOfBirth.toString(),
        userPermission = userPermissions.map { it.toResponse() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Page<User>.toResponse() = pagingResponse<UserResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}