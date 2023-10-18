/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TokenGenerator {

    private static final int TOKEN_LENGTH = 32;

    public static String generateToken() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);

        final StringBuilder tokenBuilder = new StringBuilder();
        for (byte b : tokenBytes) {
            tokenBuilder.append(String.format("%02x", b));
        }
        return tokenBuilder.toString();
    }

}
