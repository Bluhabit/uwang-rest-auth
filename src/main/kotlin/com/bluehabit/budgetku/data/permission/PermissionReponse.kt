package com.bluehabit.budgetku.data.permission

import javax.persistence.Column

data class PermissionReponse(
    var id:String? = null,
    var permissionName:String? = null,
    var permissionType:String? = null,
    var permissionGroup:String?=null,
)