/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.data.user.SignInWithGoogleRequest
import com.bluehabit.budgetku.data.user.SignInWithEmailRequest
import com.bluehabit.budgetku.data.user.SignUpWithEmailRequest
import com.bluehabit.budgetku.data.user.SignUpWithGoogleRequest
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/auth"]
)
class AuthController(
    private val authService: UserService
) {

    companion object {
        const val json = "application/json"
    }

    @PostMapping(
        value = ["/sign-in-email"],
        produces = [json],
        consumes = [json]
    )
    fun signInWithEmailAndPassword(
        @RequestBody signInWithEmailRequest: SignInWithEmailRequest,
    ) = authService.signInWithEmail(
        signInWithEmailRequest
    )

    @PostMapping(
        value = ["/sign-in-google"],
        produces = [json],
        consumes = [json]
    )
    fun signInWithGoogle(
        @RequestBody signInWithGoogleRequest: SignInWithGoogleRequest,
    ) = authService.signInWithGoogle(signInWithGoogleRequest)

    @PostMapping(
        value = ["/sign-up-email"],
        produces = [json],
        consumes = [json]
    )
    fun signUpWithEmailAndPassword(
        @RequestBody signUpWithEmailRequest: SignUpWithEmailRequest,
    ) = authService.signUpWithEmail(
        signUpWithEmailRequest
    )

    @PostMapping(
        value = ["/sign-up-google"],
        produces = [json],
        consumes = [json]
    )
    fun signUpWithGoogle(
        @RequestBody signUpWithGoogleRequest: SignUpWithGoogleRequest,
    ) = authService.signUpWithGoogle(signUpWithGoogleRequest)

    @GetMapping(
        value = ["/verification/{verificationId}"],
        produces = [json]
    )
    fun verificationUser(
        @PathVariable(name = "verificationId") token: String
    ) = authService.userVerification(token)

    @GetMapping(
        value = ["/refresh-token/{token}"],
        produces = [json]
    )
    fun refreshToken(
        @PathVariable("token") token: String?
    ) = authService.refreshToken(token)
}