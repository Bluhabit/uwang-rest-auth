package com.bluehabit.budgetku.config.tokenMiddleware


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {
    @Value("jwtSecret")
    lateinit var secret:String;

    var issuer:String = "bluehabit.com"

    @Throws(IllegalArgumentException::class,JWTCreationException::class)
    fun generateToken(email:String):String{
        return JWT
            .create()
            .withSubject("User detail")
            .withIssuedAt(Date())
            .withClaim("email",email)
            .withIssuer(issuer)
            .sign(Algorithm.HMAC512(secret))

    }

    @Throws(JWTVerificationException::class)
    fun validateTokenAndRetrieveSubject(
        token:String
    ):String{
        val verifier = JWT.require(Algorithm.HMAC512(secret))
            .withSubject("User detail")
            .withIssuer(issuer)
            .build()

        return verifier.verify(token).getClaim("email").asString()
    }
}