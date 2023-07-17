package com.bluehabit.budgetku.common;

import com.auth0.jwt.JWT;

import java.time.LocalDate;
import java.util.Optional;

public class GoogleAuthUtil {
    public static Optional<GoogleClaim> getGoogleClaim(String token) {

        try {
            var claims = JWT.decode(token).getClaims();

            var expired = claims.get("exp").asLong();
            var message = "";
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(expired))) {
                return Optional.empty();
            }

            return Optional.of(
                    new GoogleClaim(
                            claims.get("email").asString(),
                            claims.get("picture").asString(),
                            claims.get("given_name").asString(),
                            "",
                            message
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
