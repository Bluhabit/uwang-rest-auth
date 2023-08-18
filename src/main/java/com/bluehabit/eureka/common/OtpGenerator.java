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

    private static final int MAX_LENGTH_OTP = 4;
    private static final int MAX_INDEX_OTP = 3;

    public static String generateOtp() {
        final Random random = new Random();
        final int otp = MIN_OTP_VALUE + random.nextInt(MAX_OTP_VALUE);
        final String resultOtp = String.valueOf(otp);
        if (resultOtp.length() > MAX_LENGTH_OTP) {
            return resultOtp.substring(MAX_INDEX_OTP, resultOtp.length() - 1);
        }
        return resultOtp;
    }
}
