/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.config.apiKeyMiddleware.ApiKeyAuthFilter
import com.bluehabit.budgetku.config.apiKeyMiddleware.ApiKeyAuthManager
import com.bluehabit.budgetku.config.apiKeyMiddleware.CustomChainApiKey
import com.bluehabit.budgetku.data.apiKey.ApiKeyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
@EnableWebSecurity
@Order(1)
class ApiKeyConfiguration(
    private val customChainApiKey: CustomChainApiKey,
    private val apiKeyRepository: ApiKeyRepository,
    private val i18n: ResourceBundleMessageSource
) {

    @Bean
    @Throws(Exception::class, UnAuthorizedException::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        val apiKeyAuthFilter = ApiKeyAuthFilter("x-api-key")
        apiKeyAuthFilter.setAuthenticationManager(ApiKeyAuthManager(apiKeyRepository, i18n))

        http
            .csrf { csrf -> csrf.disable() }
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .authorizeHttpRequests { autz ->
                autz.requestMatchers("/v1/**")
                    .authenticated()
            }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .addFilter(apiKeyAuthFilter)



        http.addFilterBefore(
            customChainApiKey,
            LogoutFilter::class.java
        )

        http.cors {
            cors->
            cors.configurationSource(corsConfigurationSource("*"))
        }

        return http.build()
    }


}