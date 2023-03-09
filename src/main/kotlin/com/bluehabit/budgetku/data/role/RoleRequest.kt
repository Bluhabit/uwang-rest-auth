/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class RoleRequest(
    @field:NotBlank
    var roleName:String?,

    @field:NotBlank
    var roleDescription:String?,

    @field:NotEmpty
    var permissions:List<String>?
)

data class RoleUpdateRequest(
    @field:NotBlank
    var roleName:String?,

    @field:NotBlank
    var roleDescription:String?,

    @field:NotEmpty
    var permissions:List<String>?
)