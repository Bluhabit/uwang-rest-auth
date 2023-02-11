package com.bluehabit.budgetku.data.userActivity

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.data.user.UserResponse
import com.bluehabit.budgetku.data.user.toResponse
import org.springframework.data.domain.Page

data class UserActivityResponse(
    var userActivityId: String? = null,
    var userActivityDescription: String? = null,
    var userActivityType: String? = null,
    var userActivityRef: String? = null,
    var user: UserResponse? = null,
    var createdAt: String = "",
    var updatedAt: String = ""
)

fun UserActivity.toResponse() = UserActivityResponse(
    userActivityId = userActivityId,
    userActivityDescription = userActivityDescription,
    userActivityType = userActivityType,
    userActivityRef = userActivityRef,
    user = user?.toResponse(),
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)

fun Page<UserActivity>.toListResponse() = content.map { it.toResponse() }