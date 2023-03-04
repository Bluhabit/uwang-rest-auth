/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config.tokenMiddleware

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.utils.translate
import com.bluehabit.budgetku.config.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver


@Component
class CustomChainJwt(
    private val userService: UserDetailsService,
    private val jwtUtil: JwtUtil,
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver,
    private val message: ResourceBundleMessageSource
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader("Authorization")

            if (header != null &&
                header.isNotBlank()
            ) {

                val errMessage = message.translate("auth.token.invalid")
                val jwt = header.replace("Bearer ", "")
                if (jwt.isBlank()) {
                    throw UnAuthorizedException(errMessage)
                }

                val email = jwtUtil.validateTokenAndRetrieveSubject(jwt)
                    ?: throw UnAuthorizedException(errMessage)

                if (jwtUtil.isJwtExpired(jwt)) {
                    throw UnAuthorizedException(errMessage)
                }


                val user = userService
                    .loadUserByUsername(email)
                    ?: throw UnAuthorizedException(errMessage)


                val userNameAuth = UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    user.authorities

                )


                SecurityContextHolder.getContext().authentication = userNameAuth
                filterChain.doFilter(request, response)
            } else {
                filterChain.doFilter(request, response)
            }

        } catch (e: Exception) {
            resolver.resolveException(request, response, null, e)
        }
    }

}