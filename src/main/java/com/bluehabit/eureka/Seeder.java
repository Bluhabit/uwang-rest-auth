/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class Seeder implements ApplicationRunner {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    private final OffsetDateTime date = OffsetDateTime.now();
    private final LocalDate dateOfBirth = LocalDate.of(1998, 9, 16);

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
