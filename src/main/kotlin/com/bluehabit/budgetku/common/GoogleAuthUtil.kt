package com.bluehabit.budgetku.common

import com.auth0.jwt.JWT
import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserAuthProvider.GOOGLE
import com.bluehabit.budgetku.data.user.UserStatus.ACTIVE
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.core.env.Environment
import java.io.IOException
import java.security.GeneralSecurityException
import java.time.LocalDate
import java.time.OffsetDateTime

class GoogleAuthUtil(
    private val env: Environment
) {
    fun getProfile(token: String): Triple<Boolean, User?, String> {
        try {
            val claims = JWT.decode(token).claims
            val exp = claims["exp"]?.asLong() ?: 0
            if(LocalDate.now().isAfter(LocalDate.ofEpochDay(exp))){
                return Triple(false,null,"Token expired")
            }
            val user = User(
                userId = null,
                userEmail = claims["email"]?.asString().orEmpty(),
                userCountryCode = claims["locale"]?.asString().orEmpty(),
                userAuthProvider = GOOGLE,
                userPassword = "",
                userProfilePicture = claims["picture"]?.asString().orEmpty(),
                userFullName = claims["given_name"]?.asString().orEmpty(),
                userStatus = ACTIVE,
                userDateOfBirth = OffsetDateTime.now(),
                userAuthTokenProvider = token,
                userPhoneNumber = "",
                userRoles = listOf(),
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
            return Triple(true, user, "")
        } catch (e: Exception) {
            return Triple(true, null, e.message.orEmpty())
        }


    }
}