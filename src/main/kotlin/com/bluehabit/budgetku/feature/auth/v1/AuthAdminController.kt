package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.data.user.LoginRequest
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1/admin/auth"]
)
class AuthAdminController(
    private val authService: UserService
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