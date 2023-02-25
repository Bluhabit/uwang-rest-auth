package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.data.user.LoginGoogleRequest
import com.bluehabit.budgetku.data.user.LoginRequest
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@RequestMapping(
    value = ["/v1/auth"]
)
class AuthController(
    private val authService: UserService
) {
    @PostMapping(
        value = ["/sign-in-email"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signInWithEmailAndPassword(
        @RequestBody loginRequest: LoginRequest,
    ) = authService.signInWithEmailAndPassword(
        loginRequest
    )

    @PostMapping(
        value = ["/sign-in-google"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signInWithGoogle(
        @RequestBody loginGoogleRequest: LoginGoogleRequest,
    )= authService.signInWithGoogle(loginGoogleRequest)
}