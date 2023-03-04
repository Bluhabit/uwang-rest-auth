/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.auth.v1

import com.bluehabit.budgetku.data.user.SignInWithGoogleRequest
import com.bluehabit.budgetku.data.user.SignInWithEmailRequest
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
        @RequestBody signInWithEmailRequest: SignInWithEmailRequest,
    ) = authService.signInWithEmailAndPassword(
        signInWithEmailRequest
    )

    @PostMapping(
        value = ["/sign-in-google"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun signInWithGoogle(
        @RequestBody signInWithGoogleRequest: SignInWithGoogleRequest,
    )= authService.signInWithGoogle(signInWithGoogleRequest)
}