package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.data.user.LoginRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1/auth"]
)
class AuthController(
    private val authService: AuthService
) {
    @PostMapping(
        value = ["/sign-in-with-email"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signInWithEmailAndPassword(
        @RequestBody loginRequest: LoginRequest
    ) = authService.signInWithEmailAndPassword(
        loginRequest
    )
}