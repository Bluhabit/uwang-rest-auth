/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.config.tokenMiddleware.CustomChainJwt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class TokenConfiguration(
    private val customChainJwt: CustomChainJwt,
) {

    @Bean
    @Order(2)
    @Throws(Exception::class, UnAuthorizedException::class)
    fun filterChainToken(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf { csrf -> csrf.disable() }
            .httpBasic { form -> form.disable() }
            .cors()
            .and()
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/v1/auth/sign-in-email",
                    "/v1/auth/sign-in-google",
                    "/v1/auth/sign-up-email",
                    "/v1/auth/sign-up-google"
                )
                    .permitAll()

            }
            .cors()
            .and()

            .authorizeHttpRequests {
                it.requestMatchers(
                    "/v1/**"
                ).authenticated()
            }
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .exceptionHandling()
            .authenticationEntryPoint { request, response, authException ->
                response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.message)
            }


        http
            .addFilterBefore(
                customChainJwt,
                UsernamePasswordAuthenticationFilter::class.java
            )


        http.cors { cors ->
            cors.configurationSource(corsConfigurationSource("*"))
        }

        return http.build()
    }
}