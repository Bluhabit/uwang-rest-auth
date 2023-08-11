/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.user.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class ResetPasswordService {

    private UserVerificationRepository userVerificationRepository;
    public ResponseEntity<BaseResponse<Object>> reset(String token, String newPassword) {

        return  BaseResponse.success("password berhasil diupdate", new HashMap<>());
    }
}
