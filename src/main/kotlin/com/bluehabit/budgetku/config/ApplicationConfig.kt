/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.common.Constants.BCrypt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*


@Configuration
class ApplicationConfig {

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter? {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        loggingFilter.setMaxPayloadLength(64000)
        return loggingFilter
    }
    @Bean
    fun localResolver(): SessionLocaleResolver {
        val resolver = SessionLocaleResolver()
        resolver.setDefaultLocale(Locale.US)
        return resolver
    }

    @Bean
    fun bundleMessageSource(): ResourceBundleMessageSource {
        val base = ResourceBundleMessageSource()
        base.setBasename("message")

        return base
    }
    @Bean
    fun scrypt(): SCryptPasswordEncoder = SCryptPasswordEncoder(
        2,
        1,
        1,
        10,
        10
    )
}