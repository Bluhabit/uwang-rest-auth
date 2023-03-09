/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role

import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.role.permission.Permission
import com.bluehabit.budgetku.data.role.roleGroup.RoleGroup
import org.springframework.data.domain.Page
import java.time.OffsetDateTime


@JvmName("pagingPermissionResponse")
fun Page<Permission>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it}
    totalData = totalElements
    totalPagesCount = totalPages
}

@JvmName("pagingRoleGroupResponse")
fun Page<RoleGroup>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it}
    totalData = totalElements
    totalPagesCount = totalPages
}