/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services.user;

import com.bluehabit.eureka.common.JwtUtils;
import com.bluehabit.eureka.component.user.UserRepository;
import com.bluehabit.eureka.component.user.model.SignInRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<Map<String, String>> signIn(
            SignInRequest request
    ) {
        return userRepository.findByUserEmail(request.email())
                .map((user) -> {
                    if (!encoder.matches(request.password(), user.getUserPassword())) {
                        return ResponseEntity.status(400).body(
                                Map.ofEntries(
                                        Map.entry("statusCode", "400"),
                                        Map.entry("data", ""),
                                        Map.entry("message", "Username atau password tidak sesuai")

                                )
                        );
                    }

                    return ResponseEntity.status(200).body(
                            Map.ofEntries(
                                    Map.entry("statusCode", "200"),
                                    Map.entry("data", jwtUtils.generateToken(user.getUserEmail())),
                                    Map.entry("message", "Username atau password tidak sesuai")

                            )
                    );
                }).orElseGet(() -> ResponseEntity.status(400).body(
                        Map.ofEntries(
                                Map.entry("statusCode", "400"),
                                Map.entry("data", ""),
                                Map.entry("message", "Username atau password tidak sesuai")

                        )
                ));

    }
}
