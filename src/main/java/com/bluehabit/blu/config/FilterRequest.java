/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.config;

import com.bluehabit.blu.common.JwtUtil;
import com.bluehabit.blu.exception.UnAuthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Locale;

@Component
public class FilterRequest extends OncePerRequestFilter {
    private static final int LENGTH_BEARER = 6;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private ResourceBundleMessageSource i81n;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final Locale locale = setLocale(request);
            if (new AntPathMatcher().match("/v1/auth/**", request.getServletPath()) || new AntPathMatcher().match("/api/v1/auth/**", request.getServletPath())) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {

                throw new UnAuthorizedException(i81n.getMessage("auth.header.empty", null, locale));
            }

            if (!authHeader.startsWith("Bearer")) {
                throw new UnAuthorizedException(i81n.getMessage("auth.header.not.contain.bearer", null, locale));
            }

            if (authHeader.length() <= LENGTH_BEARER) {
                throw new UnAuthorizedException(i81n.getMessage("auth.header.only.contain.bearer", null, locale));
            }

            final String token = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(token);

            if (username.isEmpty()) {
                throw new UnAuthorizedException(i81n.getMessage("auth.header.cannot.extract.claim", null, locale));
            }

            final UserDetails userDetails = userService.loadUserByUsername(username);
            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new UnAuthorizedException(i81n.getMessage("auth.header.user.not.found", null, locale));
            }

            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (UnAuthorizedException exception) {
            resolver.resolveException(request, response, null, exception);
        }
    }

    private Locale setLocale(HttpServletRequest request) {
        final String locale = request.getHeader("Accept-Language");
        Locale finalLocale = Locale.forLanguageTag("ID");
        if (!locale.isEmpty() && !locale.isBlank()) {
            finalLocale = Locale.forLanguageTag(locale);
            LocaleContextHolder.setDefaultLocale(finalLocale);
        } else {
            LocaleContextHolder.setLocale(finalLocale);
        }
        return finalLocale;
    }
}
