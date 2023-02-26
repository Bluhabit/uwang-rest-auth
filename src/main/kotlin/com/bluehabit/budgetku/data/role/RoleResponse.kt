package com.bluehabit.budgetku.data.role

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.permission.PermissionReponse
import org.springframework.data.domain.Page


data class RoleResponse(
    var roleId: String? = null,
    var roleName: String? = null,
    var roleDescription: String? = null,
    var permission:List<PermissionReponse> = listOf(),
    var createdAt: String="",
    var updatedAt: String = "",
)

fun Role.toResponse() = RoleResponse(
    roleId = roleId,
    roleName = roleName,
    roleDescription = roleDescription,
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt =  updatedAt.fromOffsetDatetime()
)
fun Page<Role>.toResponse() = pagingResponse<RoleResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}