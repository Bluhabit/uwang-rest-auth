package com.bluehabit.budgetku.config.tokenMiddleware


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
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
        .withIssuedAt(Date())
        .withExpiresAt(Date(OffsetDateTime.now().plusHours(24).toEpochSecond()))
        .withClaim("email", email)
        .withIssuer(issuer)
        .sign(Algorithm.HMAC512(secret))


    @Throws(JWTVerificationException::class)
    fun validateTokenAndRetrieveSubject(
        token: String
    ): String {

        val decode = JWT.decode(token).getClaim("email").asString()


        return decode
    }
    @Throws(JWTDecodeException::class)
    fun isJwtExpired(token: String) = JWT.decode(token).expiresAt.before(Date(OffsetDateTime.now().toEpochSecond()))
}