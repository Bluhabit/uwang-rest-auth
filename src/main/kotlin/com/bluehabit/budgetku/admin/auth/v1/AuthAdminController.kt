package com.bluehabit.budgetku.admin.auth.v1

import com.bluehabit.budgetku.user.LoginRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1/admin/auth"]
)
class AuthAdminController(
    private val authService: AuthAdminService
) {
    @PostMapping(
        value = ["/sign-in"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signIn(
        @RequestBody body: LoginRequest
    ) = authService.signIn(body)

}