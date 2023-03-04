/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userActivity

import com.bluehabit.budgetku.common.utils.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.user.UserProfileResponse
import com.bluehabit.budgetku.data.user.UserResponse
import com.bluehabit.budgetku.data.user.toResponse
import org.springframework.data.domain.Page

data class UserActivityResponse(
    var userActivityId: String? = null,
    var userActivityDescription: String? = null,
    var userActivityType: String? = null,
    var userActivityRef: String? = null,
    var user: UserProfileResponse? = null,
    var createdAt: String = "",
    var updatedAt: String = ""
)

fun UserActivity.toResponse() = UserActivityResponse(
    userActivityId = userActivityId,
    userActivityDescription = userActivityDescription,
    userActivityType = userActivityType,
    userActivityRef = userActivityRef,
    user = userProfile?.toResponse(),
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)

fun Page<UserActivity>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}