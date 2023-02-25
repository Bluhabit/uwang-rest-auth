package com.bluehabit.budgetku.config.apiKeyMiddleware

import com.bluehabit.budgetku.data.apiKey.ApiKeyRepository
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.translate
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication

class ApiKeyAuthManager(
    private val apiKeyRepository: ApiKeyRepository,
    private val message:ResourceBundleMessageSource
) : AuthenticationManager {
    @Throws(UnAuthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication {

        val apiKey = authentication?.principal as String
        if(apiKey.isEmpty()){
            throw UnAuthorizedException(
                message.translate("auth.apikey.invalid")
            )
        }
        val find = apiKeyRepository.findTopByValue(apiKey)
        if (find == null) {
            throw UnAuthorizedException(
                message.translate("auth.apikey.invalid")
            )
        } else {
            authentication?.isAuthenticated = true
            return authentication!!
        }
    }


}