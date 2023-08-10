/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services;

import com.bluehabit.eureka.common.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService {
    public ResponseEntity<BaseResponse<Object>> reset(String token, String newPassword) {
        System.out.println(token);
        System.out.println(newPassword);
        return BaseResponse.success(
            "success",
            new Object()
        );
    }
}
