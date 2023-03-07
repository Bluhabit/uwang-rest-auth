/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.v1

import com.bluehabit.budgetku.data.user.UserService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/admin"]
)
class UserAdminController(
    private val userService: UserService
) {

    @GetMapping(
        value = ["/tes"],
        produces = ["application/json"]
    )
    fun tes(
        pageable: Pageable
    )= userService.getAllUsers(pageable)

}