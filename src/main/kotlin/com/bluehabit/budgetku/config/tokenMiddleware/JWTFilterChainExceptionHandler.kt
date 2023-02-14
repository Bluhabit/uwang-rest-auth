package com.bluehabit.budgetku.config.tokenMiddleware

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential
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
    private val resolver: HandlerExceptionResolver
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader("Authorization")
            if (header.isEmpty()) {
                return filterChain.doFilter(request, response)
            }

            val jwt = header.replace("Bearer ", "")
            if (jwt.isEmpty()) throw UnAuthorizedException("No token provided")

            if (jwtUtil.isJwtExpired(jwt)) throw UnAuthorizedException("Token is not valid or expired")

            val email = jwtUtil.validateTokenAndRetrieveSubject(jwt)

            val user = userService
                .loadUserByUsername(email)
                ?: throw UnAuthorizedException("Token is not valid or expired")

            val userNameAuth = UsernamePasswordAuthenticationToken(
                email,
                null,
                user.authorities
            )
            SecurityContextHolder.getContext().authentication = userNameAuth
            return filterChain.doFilter(request, response)
        } catch (e: Exception) {
            resolver.resolveException(request, response, null, e)
        }
    }

}