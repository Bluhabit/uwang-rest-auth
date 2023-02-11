package com.bluehabit.budgetku.data.permission

import java.time.OffsetDateTime
import javax.persistence.Column

data class PermissionReponse(
    var id:String? = null,
    var permissionName:String? = null,
    var permissionType:String? = null,
    var permissionGroup:String?=null,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)