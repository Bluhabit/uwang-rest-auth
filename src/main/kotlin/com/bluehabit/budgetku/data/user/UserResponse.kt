package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.data.permission.PermissionReponse
import com.bluehabit.budgetku.data.role.RoleResponse
import java.time.OffsetDateTime


data class UserResponse(
    var userId: String?=null,
    var userFullName:String,
    var userCountryCode:String,
    var userDateOfBirth:String,
    var userEmail: String,
    var userLevel: LevelUser,
    var userAuthProvider: UserAuthProvider,
    var userPermission:List<PermissionReponse>,
    var userRoles:List<RoleResponse>,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)

fun User.toResponse():UserResponse {
    val permission = mutableListOf<PermissionReponse>()

    val role = userRoles.map {
        permission += it.permissions.map { p->
            PermissionReponse(
                id = p.id,
                permissionName = p.permissionName,
                permissionGroup = p.permissionGroup,
                permissionType = p.permissionType
            )
        }
        RoleResponse(
            roleName = it.roleName,
            roleDescription = it.roleDescription,
            roleId = it.roleId
        )
    }

    return UserResponse(
        userId = userId,
        userFullName = userFullName,
        userEmail = userEmail,
        userAuthProvider = userAuthProvider,
        userCountryCode = userCountryCode,
        userDateOfBirth = userDateOfBirth.toString(),
        userLevel = userLevel,
        userPermission =permission,
        userRoles = role,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}