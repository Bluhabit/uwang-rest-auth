package com.bluehabit.budgetku.config.apiKeyMiddleware

import com.bluehabit.budgetku.admin.apiKey.v1.ApiKeyRepository
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class ApiKeyAuthManager(
    private val apiKeyRepository: ApiKeyRepository
) : AuthenticationManager {
    @Throws(UnAuthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication {

        val apiKey = authentication?.principal as String
        if(apiKey.isEmpty()){
            throw UnAuthorizedException(
                "Application is not permitted for use this resource"
            )
        }
        val find = apiKeyRepository.findTopByValue(apiKey)
        if (find == null) {
            throw UnAuthorizedException(
                "Application is not permitted for use this resource"
            )
        } else {
            authentication?.isAuthenticated = true
            return authentication!!
        }
    }


}