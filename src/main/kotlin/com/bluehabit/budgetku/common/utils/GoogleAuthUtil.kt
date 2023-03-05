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

data class GoogleClaim(
    var email:String = "",
    var picture:String ="",
    var fullName:String="",
    var locale:String="",
    var valid:Boolean,
    var message:String
)
class GoogleAuthUtil(
    private val env: Environment,
    private val messageSource: ResourceBundleMessageSource
) {

    fun getGoogleClaim(token:String):GoogleClaim{
        try {
            val claims = JWT.decode(token).claims
            val exp = claims["exp"]?.asLong() ?: 0
            var message = messageSource.translate("auth.token.invalid")

            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(exp))) {
                return GoogleClaim(
                    valid = false,
                    message = message
                )
            }
            return GoogleClaim(
                valid = true,
                message = "",
                email = claims["email"]?.asString().orEmpty(),
                picture = claims["picture"]?.asString().orEmpty(),
                fullName = claims["given_name"]?.asString().orEmpty(),

            )
        } catch (e: Exception) {
            return GoogleClaim(
                valid = false,
                message = e.message.orEmpty()
            )
        }
    }

}