package com.bluehabit.budgetku.common

import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserAuthProvider.GOOGLE
import com.bluehabit.budgetku.data.user.UserStatus.ACTIVE
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.core.env.Environment
import java.io.IOException
import java.security.GeneralSecurityException
import java.time.OffsetDateTime

class GoogleAuthUtil(
    private val env: Environment
) {
    fun getProfile(token: String): User? {
        val verifier = GoogleIdTokenVerifier.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory(),
        )
            .setAudience(
                listOf(
                    env.getProperty("googleClientId")
                )
            )
            .build()


        return try {

            val idToken = verifier.verify(token)
            return if (idToken != null) {
                val payload = idToken.payload

                User(
                    userId = null,
                    userEmail = payload.email,
                    userCountryCode = payload["locale"] as String,
                    userAuthProvider = GOOGLE,
                    userPassword = "",
                    userProfilePicture = payload["picture"] as String,
                    userFullName = payload["name"] as String,
                    userStatus = ACTIVE,
                    userDateOfBirth = OffsetDateTime.now(),
                    userAuthTokenProvider = token,
                    userPhoneNumber = "",
                    userRoles = listOf(),
                    createdAt = OffsetDateTime.now(),
                    updatedAt = OffsetDateTime.now()
                )
            } else null


        }catch (e:Exception){
            null
        }
    }
}