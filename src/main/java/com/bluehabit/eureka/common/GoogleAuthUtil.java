/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class GoogleAuthUtil {
    public static Optional<GoogleClaim> getGoogleClaim(String token) {

        try {
            final Map<String, Claim> claims = JWT.decode(token).getClaims();

            final Long expired = claims.get("exp").asLong();
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(expired))) {
                return Optional.empty();
            }

            return Optional.of(
                new GoogleClaim(
                    claims.get("email").asString(),
                    claims.get("picture").asString(),
                    claims.get("given_name").asString(),
                    Locale.forLanguageTag("ID").getLanguage(),
                    ""
                )
            );
        } catch (JWTDecodeException decodeException) {
            return Optional.empty();
        }
    }
}
