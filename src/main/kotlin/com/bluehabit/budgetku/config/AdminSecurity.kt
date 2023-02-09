package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.config.adminMiddleware.JWTFilterChainExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class AdminSecurity(
    private val jwtFilterChainExceptionHandler: JWTFilterChainExceptionHandler,
    private val userDetailsService: UserDetailsService
):WebSecurityConfigurerAdapter(){
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        val corsOrigin = "*"


        http
            .csrf()
            .disable()
            .httpBasic()
            .disable()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers("/api/v1/admin/sign-in").permitAll()
            .antMatchers("/api/v1/admin/**")
            .hasAnyRole("USER")
            .and()
            .userDetailsService(userDetailsService)
            .exceptionHandling()
            .authenticationEntryPoint{
                req,res,ex->
                res.sendError(HttpStatus.UNAUTHORIZED.value(),ex.message)
            }
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)



        http
            .addFilterBefore(jwtFilterChainExceptionHandler,UsernamePasswordAuthenticationFilter::class.java)

        http.cors()
            .configurationSource(corsConfigurationSource(corsOrigin))

    }


    @Bean
    fun passwordEncoder():PasswordEncoder{
        return BCryptPasswordEncoder()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    private fun corsConfigurationSource(corsOrigin: String): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("*")
        configuration.allowedOrigins = listOf(corsOrigin)
        configuration.allowedMethods = listOf(
            "GET", "POST", "HEAD", "OPTIONS", "PUT", "PATCH", "DELETE"
        )
        configuration.maxAge = 10L
        //when this true the origin = * cannot be used any more
        // configuration.allowCredentials = true
        configuration.allowedHeaders = listOf(
            "Accept",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Access-Control-Allow-Origin",
            "Access-Control-Expose-Headers",
            "Accept-Language",
            "Authorization",
            "Content-Type",
            "Request-Name",
            "Request-Surname",
            "Origin",
            "X-Api-Key",
            "X-Request-AppVersion",
            "X-Request-OsVersion",
            "X-Request-Device",
            "X-Requested-With"
        )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}