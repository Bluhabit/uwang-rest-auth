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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            setLocale(request);
            if (new AntPathMatcher().match("/v1/auth/**", request.getServletPath())) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                throw new UnAuthorizedException("header empty");
            }

            if (!authHeader.startsWith("Bearer")) {
                throw new UnAuthorizedException("header doesn't contain Bearer");
            }

            if (authHeader.length() <= LENGTH_BEARER) {
                throw new UnAuthorizedException("Header only contains 'Bearer'");
            }

            final String token = authHeader.substring(7);
            final String username = jwtUtil.extractUsername(token);

            if (username.isEmpty()) {
                throw new UnAuthorizedException("failed extract claim");
            }

            final UserDetails userDetails = userService.loadUserByUsername(username);
            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new UnAuthorizedException("user not found");
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

    private void setLocale(HttpServletRequest request) {
        final String locale = request.getHeader("Accept-Language");
        if (!locale.isEmpty() && !locale.isBlank()) {
            LocaleContextHolder.setDefaultLocale(Locale.forLanguageTag(locale));
        } else {
            LocaleContextHolder.setLocale(Locale.forLanguageTag("ID"));
        }
    }
}
