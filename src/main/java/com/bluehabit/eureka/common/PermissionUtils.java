/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class PermissionUtils {
    public static Optional<Boolean> hasAccess(String... permissions) {
        var authority = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if (new HashSet<>(authority).containsAll(Arrays.stream(permissions).toList())) {
            return Optional.of(true);
        }

        return Optional.empty();
    }
}
