package com.bluehabit.budgetku.data.role

import java.time.OffsetDateTime


data class RoleResponse(
    var roleId: String? = null,
    var roleName: String? = null,
    var roleDescription: String? = null,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime,
)