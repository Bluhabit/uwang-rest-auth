/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import com.auth0.jwt.JWT
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.UserAuthProvider.GOOGLE
import com.bluehabit.budgetku.data.user.UserStatus.ACTIVE
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.env.Environment
import java.time.LocalDate
import java.time.OffsetDateTime

class GoogleAuthUtil(
    private val env: Environment,
    private val messageSource: ResourceBundleMessageSource
) {
    fun getCredential(token: String): Triple<Boolean, UserCredential?, String> {
        try {
            val claims = JWT.decode(token).claims
            val exp = claims["exp"]?.asLong() ?: 0
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(exp))) {
                return Triple(false, null, messageSource.translate("auth.token.invalid"))
            }
            val userCredential = UserCredential(
                userId = null,
                userEmail = claims["email"]?.asString().orEmpty(),
                userAuthProvider = GOOGLE.name,
                userPassword = "",
                userStatus = ACTIVE.name,
                userAuthTokenProvider = token,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            return Triple(true, userCredential, "")
        } catch (e: Exception) {
            return Triple(true, null, e.message.orEmpty())
        }


    }

    fun getProfile(token: String): Triple<Boolean, UserProfile?, String> {
        try {
            val claims = JWT.decode(token).claims
            val exp = claims["exp"]?.asLong() ?: 0
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(exp))) {
                return Triple(false, null, messageSource.translate("auth.token.invalid"))
            }
            val userCredential = UserProfile(
                userId = null,
                userCountryCode = claims["locale"]?.asString().orEmpty(),
                userProfilePicture = claims["picture"]?.asString().orEmpty(),
                userFullName = claims["given_name"]?.asString().orEmpty(),
                userDateOfBirth = LocalDate.now(),
                userPhoneNumber = "",
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            return Triple(true, userCredential, "")
        } catch (e: Exception) {
            return Triple(true, null, e.message.orEmpty())
        }


    }
}