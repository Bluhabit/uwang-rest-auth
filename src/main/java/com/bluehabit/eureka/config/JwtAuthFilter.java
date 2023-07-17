/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.config;

import com.bluehabit.eureka.exception.UnAuthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userService;

    private List<String> allowList = List.of(
            "/auth/sign-in-email",
            "/auth/sign-in-google",
            "/auth/sign-up-email",
            "/auth/sign-up-google",
            "/auth/refresh-token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       // resolver.resolveException(request,response,null, new UnAuthorizedException("invalid"));

        try {

            if (allowList.contains(request.getServletPath())) {
                filterChain.doFilter(request, response);
                return;
            }
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isEmpty()) {
                throw new UnAuthorizedException("header empty");
            }

            if (!authHeader.startsWith("Bearer")) {
                throw new UnAuthorizedException("header doesn't contain Bearer");
            }

            if(authHeader.length() <= 6){
                throw new UnAuthorizedException("Header only contains 'Bearer'");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username.isEmpty()) {
                throw new UnAuthorizedException("failed extract claim");
            }

            UserDetails userDetails = userService.loadUserByUsername(username);
            if (!jwtService.validateToken(token, userDetails)) {
                throw new UnAuthorizedException("user not found");
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }

    }
}
