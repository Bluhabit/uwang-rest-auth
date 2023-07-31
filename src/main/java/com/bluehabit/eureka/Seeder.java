/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka;

import com.bluehabit.eureka.component.user.User;
import com.bluehabit.eureka.component.user.UserRepository;
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
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var user = new User();
        var idUSer = "iniIdnya";

        user.setId(idUSer);
        user.setUserEmail("trian@gmail.com");
        user.setUserPassword(encoder.encode("12345678"));
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        if (userRepository.findById(idUSer).isEmpty()) {
            userRepository.save(user);
        }

    }
}
