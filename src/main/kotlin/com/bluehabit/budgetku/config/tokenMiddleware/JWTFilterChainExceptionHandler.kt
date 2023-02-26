/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config.tokenMiddleware

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.translate
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JWTFilterChainExceptionHandler(
    private val userRepository: UserRepository,
    private val userService: UserService,
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

                val jwt = header.replace("Bearer ", "")
                if (jwt.isBlank()) {
                    throw UnAuthorizedException(message.translate("auth.token.invalid"))
                }

                if (jwtUtil.isJwtExpired(jwt)) {
                    throw UnAuthorizedException(message.translate("auth.token.invalid"))
                }

                val email = jwtUtil.validateTokenAndRetrieveSubject(jwt)


                val user = userService
                    .loadUserByUsername(email)
                    ?: throw UnAuthorizedException(message.translate("auth.token.invalid"))


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