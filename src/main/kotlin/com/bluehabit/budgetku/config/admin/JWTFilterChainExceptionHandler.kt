package com.bluehabit.budgetku.config.admin

import com.bluehabit.budgetku.admin.auth.AuthServiceImpl
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import org.springframework.beans.factory.annotation.Qualifier
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
    private val userDetailsService: AuthServiceImpl,
    private val jwtUtil: JwtUtil,
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver
) : OncePerRequestFilter(){
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val header = request.getHeader("Authorization")
            if(header != null &&
                header.isNotBlank()){

                val jwt = header.replace("Bearer ","")
                if(jwt.isBlank()) {
                    throw UnAuthorizedException("Token not provided 1")
                }

                val email = jwtUtil.validateTokenAndRetrieveSubject(jwt)

                val user = userDetailsService
                    .loadUserByUsername(email)

                val userNameAuth = UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    user.authorities
                )
                SecurityContextHolder.getContext().authentication = userNameAuth
                filterChain.doFilter(request, response)

            }else{

                filterChain.doFilter(request, response)
            }

        }catch (e:Exception){
            resolver.resolveException(request,response,null,e)
        }

    }

}