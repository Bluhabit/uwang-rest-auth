package com.bluehabit.budgetku.config.adminMiddleware

import com.bluehabit.budgetku.admin.auth.v1.UserRepository
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class AdminAuthManager(
    private val userRepository: UserRepository,
    private val environment: Environment
):AuthenticationManager {
    val jwtKey = environment.getProperty("jwtSecret")

    @Throws(UnAuthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication {

        val jwt = authentication?.principal as String




        if(jwt.isBlank()){
            throw UnAuthorizedException(
                "You don't have access for this resource. Please contact your admin for get the access"
            )
        }else{

            authentication.isAuthenticated = true
            return authentication
        }
    }
}