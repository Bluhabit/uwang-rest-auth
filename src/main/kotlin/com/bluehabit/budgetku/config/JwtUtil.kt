/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config;


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.bluehabit.budgetku.common.utils.getExpiredDate
import com.bluehabit.budgetku.common.utils.getTodayDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.util.*

@Component
class JwtUtil {
    @Value("jwtSecret")
    lateinit var secret: String;

    var issuer: String = "bluehabit.com"

    @Throws(IllegalArgumentException::class, JWTCreationException::class)
    fun generateToken(email: String): String = JWT
        .create()
        .withSubject("User detail")
        .withIssuedAt(getTodayDateTime().toInstant(ZoneOffset.UTC))
        .withExpiresAt(getExpiredDate())
        .withNotBefore(getTodayDateTime().toInstant(ZoneOffset.UTC))
        .withClaim("email", email)
        .withIssuer(issuer)
        .sign(Algorithm.HMAC512(secret))


    @Throws(JWTDecodeException::class)
    fun validateTokenAndRetrieveSubject(
        token: String
    ): Pair<Boolean, String> {
        return try {
            val verify = JWT.require(Algorithm.HMAC512(secret))
                .withIssuer(issuer)
                .build()
                .verify(token)

            val email = verify.getClaim("email").asString()
            Pair(true, email)
        } catch (e: Exception) {
            Pair(false, e.message.orEmpty())
        }
    }

    @Throws(JWTDecodeException::class)
    fun decode(
        token: String
    ): Pair<Boolean, String> {

            val verify = JWT.decode(token).getClaim("email").asString()

           return Pair(true, verify)
    }

}