/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.permission

import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime

data class PermissionReponse(
    var permissionId:String? = null,
    var permissionName:String? = null,
    var permissionType:String? = null,
    var permissionGroup:String?=null,
    var createdAt: OffsetDateTime?,
    var updatedAt: OffsetDateTime?
)

fun Permission.toResponse() = PermissionReponse(
    permissionId = permissionId,
    permissionName = permissionName,
    permissionType = permissionType,
    permissionGroup = permissionGroup,
    createdAt = createdAt,
    updatedAt = updatedAt
)
fun Page<Permission>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}