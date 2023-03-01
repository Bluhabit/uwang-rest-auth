/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

import com.auth0.jwt.JWT
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserAuthProvider.GOOGLE
import com.bluehabit.budgetku.data.user.UserStatus.ACTIVE
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.env.Environment
import java.time.LocalDate
import java.time.OffsetDateTime

class GoogleAuthUtil(
    private val env: Environment,
    private val messageSource: ResourceBundleMessageSource
) {
    fun getProfile(token: String): Triple<Boolean, User?, String> {
        try {
            val claims = JWT.decode(token).claims
            val exp = claims["exp"]?.asLong() ?: 0
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(exp))) {
                return Triple(false, null, messageSource.translate("auth.token.invalid"))
            }
            val user = User(
                userId = null,
                userEmail = claims["email"]?.asString().orEmpty(),
                userCountryCode = claims["locale"]?.asString().orEmpty(),
                userAuthProvider = GOOGLE.name,
                userPassword = "",
                userProfilePicture = claims["picture"]?.asString().orEmpty(),
                userFullName = claims["given_name"]?.asString().orEmpty(),
                userStatus = ACTIVE.name,
                userDateOfBirth = OffsetDateTime.now(),
                userAuthTokenProvider = token,
                userPhoneNumber = "",
             //   userRoles = listOf(),
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            return Triple(true, user, "")
        } catch (e: Exception) {
            return Triple(true, null, e.message.orEmpty())
        }


    }
}