/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class ApplicationConfig {
    private final int maxPayload = 64000;

    @Bean
    public final CommonsRequestLoggingFilter requestLoggingFilter() {
        final CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(maxPayload);
        return loggingFilter;
    }

    @Bean
    public final SessionLocaleResolver localResolver() {
        final SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }

    @Bean
    public final ResourceBundleMessageSource bundleMessageSource() {
        final ResourceBundleMessageSource message = new ResourceBundleMessageSource();
        message.setBasename("message");
        return message;
    }
}
