package com.bluehabit.budgetku.admin.auth.v1

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
    value = ["/api/v1/admin/auth"]
)
class AuthController(
    private val authService: AuthService
) {
    @GetMapping(
        value = ["/users"],
        produces = ["application/json"]
    )
    fun getListUser(
        pageable: Pageable
    ) = authService.getListUsers(pageable)

    @PostMapping(
        value = ["/sign-in"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signIn(
        @RequestBody body: LoginRequest
    ) = authService.signIn(body)

    @PostMapping(
        value = ["/user"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun createUser(
        @RequestBody body: UserRequest
    ) = authService.addNewUser(body)


    @PutMapping(
        value = ["/user/reset-password"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun resetPassword(
        @RequestBody body: ResetPasswordRequest
    ) = authService.resetPassword(body)

    @DeleteMapping(
        value = ["/user/{user_id}"],
        produces = ["application/json"]
    )
    fun deleteUser(
        @PathVariable("user_id") userId: Long
    ) = authService.deleteUser(userId)
}