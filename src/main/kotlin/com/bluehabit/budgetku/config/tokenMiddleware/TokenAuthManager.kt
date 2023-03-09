/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config.tokenMiddleware

import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class TokenAuthManager(
    private val userCredentialRepository: UserCredentialRepository,
    private val environment: Environment
) : AuthenticationManager {
    val jwtKey = environment.getProperty("jwtSecret")

    @Throws(UnAuthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication {
        val jwt = authentication?.principal as String
        if (jwt.isBlank()) {
            throw UnAuthorizedException(
                "Session is invalid or expired!"
            )
        } else {
            authentication.isAuthenticated = true
            return authentication
        }
    }
}