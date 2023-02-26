package com.bluehabit.budgetku.data.permission

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page

data class PermissionReponse(
    var permissionId:String? = null,
    var permissionName:String? = null,
    var permissionType:String? = null,
    var permissionGroup:String?=null,
    var createdAt: String="",
    var updatedAt: String="",
)

fun Permission.toResponse() = PermissionReponse(
    permissionId = permissionId,
    permissionName = permissionName,
    permissionType = permissionType,
    permissionGroup = permissionGroup,
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)
fun Page<Permission>.toResponse() = pagingResponse<PermissionReponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}