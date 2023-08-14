/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.common;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpGenerator {
    private static final int MIN_OTP_VALUE = 1000;
    private static final int MAX_OTP_VALUE = 9999;

    public static String generateOtp() {
        final Random random = new Random();
        final int otp = MIN_OTP_VALUE + random.nextInt(MAX_OTP_VALUE);
        return String.valueOf(otp);
    }
}
