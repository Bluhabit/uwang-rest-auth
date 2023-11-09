/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu;

import com.bluehabit.blu.component.AuthProvider;
import com.bluehabit.blu.component.UserStatus;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import com.bluehabit.blu.component.data.useCredential.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class Seeder implements ApplicationRunner {
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userCredentialRepository.findById("6b91eca7-335c-43f9-9c6e-c770a2f6ea6a").isEmpty()) {
            final OffsetDateTime date = OffsetDateTime.now();
            final String email = "admin@bluhabit.id";
            final String password = "12345678";
            //no empty
            final UserCredential user = new UserCredential();
            user.setId("6b91eca7-335c-43f9-9c6e-c770a2f6ea6a");
            user.setEmail(email);
            user.setStatus(UserStatus.ACTIVE);
            user.setPassword(encoder.encode(password));
            user.setCreatedAt(date);
            user.setUpdatedAt(date);
            user.setAuthProvider(AuthProvider.BASIC);

            final UserCredential saved = userCredentialRepository.save(user);
        }

    }
}
