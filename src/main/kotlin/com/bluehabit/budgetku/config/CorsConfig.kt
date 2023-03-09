/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config

import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

fun corsConfigurationSource(corsOrigin: String): CorsConfigurationSource {
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