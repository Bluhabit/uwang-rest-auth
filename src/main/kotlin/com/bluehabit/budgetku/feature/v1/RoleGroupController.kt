/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.v1

import com.bluehabit.budgetku.data.role.RoleRequest
import com.bluehabit.budgetku.data.role.RoleService
import com.bluehabit.budgetku.data.role.RoleUpdateRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/role"]
)
class RoleGroupController(
    private val roleService: RoleService
) {
    companion object {
        const val json = "application/json"
    }

    @GetMapping(
        value = ["/list-role"],
        produces = [json]
    )
    suspend fun getListRole(
        pageable: Pageable
    ) = roleService.getListRoleGroup(pageable)

    @PostMapping(
        value = ["/create-role"],
        produces = [json],
        consumes = [json]
    )
    suspend fun createRole(
        @RequestBody request: RoleRequest
    ) = roleService.createNewRoleGroup(request)

    @PutMapping(
        value = ["/update-role/{roleId}"],
        produces = [json],
        consumes = [json]
    )
    suspend fun updateRole(
        @PathVariable("roleId") roleId: String,
        @RequestBody request: RoleUpdateRequest
    ) = roleService.updateRoleGroup(
        roleId,
        request
    )

    @DeleteMapping(
        value = ["/delete-role/{roleId}"],
        produces = [json],
        consumes = [json]
    )
    suspend fun deleteRole(
        @PathVariable("roleId") roleId: String
    ) = roleService.deleteRole(roleId)
}