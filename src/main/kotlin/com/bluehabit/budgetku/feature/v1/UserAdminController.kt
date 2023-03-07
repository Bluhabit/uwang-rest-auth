/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.v1

import com.bluehabit.budgetku.data.user.AssignPermissionRequest
import com.bluehabit.budgetku.data.user.BannedUserRequest
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/admin"]
)
class UserAdminController(
    private val userService: UserService
) {
    companion object {
        const val json = "application/json"
    }

    @GetMapping(
        value = ["/list-user"],
        produces = [json]
    )
    suspend fun getListUser(
        pageable: Pageable
    ) = userService.getAllUsers(pageable)

    @PostMapping(
        value = ["/assign-permission"],
        produces = [json],
        consumes = [json]
    )
    suspend fun assignUserPermission(
        @RequestBody request: AssignPermissionRequest
    ) = userService.assignPermission(request)

    @PostMapping(
        value = ["/banned-user"],
        produces = [json],
        consumes = [json]
    )
    suspend fun bannedUser(
        @RequestBody request: BannedUserRequest
    ) = userService.bannedUser(request)

    @PostMapping(
        value = ["/activate-user"],
        produces = [json],
        consumes = [json]
    )
    suspend fun activateUser(
        @RequestBody request: BannedUserRequest
    ) = userService.bannedUser(request)
}