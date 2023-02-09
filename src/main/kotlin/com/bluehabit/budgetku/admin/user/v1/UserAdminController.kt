package com.bluehabit.budgetku.admin.user.v1

import com.bluehabit.budgetku.data.user.ResetPasswordRequest
import com.bluehabit.budgetku.data.user.CreateUserRequest
import com.bluehabit.budgetku.data.user.UserService
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
    value = ["/api/v1/admin"]
)
class UserAdminController(
    private val userService: UserService
) {

    @GetMapping(
        value = ["/users"],
        produces = ["application/json"]
    )
    fun getListUser(
        pageable: Pageable
    ) = userService.getListUsers(pageable)
    @PostMapping(
        value = ["/user"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun createUser(
        @RequestBody body: CreateUserRequest
    ) = userService.addNewUser(body)

    @PutMapping(
        value = ["/user/reset-password"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun resetPassword(
        @RequestBody body: ResetPasswordRequest
    ) = userService.resetPassword(body)

    @DeleteMapping(
        value = ["/user/{user_id}"],
        produces = ["application/json"]
    )
    fun deleteUser(
        @PathVariable("user_id") userId: Long
    ) = userService.deleteUser(userId)
}