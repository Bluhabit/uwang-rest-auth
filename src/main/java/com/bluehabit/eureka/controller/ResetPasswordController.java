/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.controller;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.user.model.ResetPasswordRequest;
import com.bluehabit.eureka.services.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetPasswordController {
    @Autowired
    private ResetPasswordService resetPasswordService;

    @PostMapping(path = "/api/v1/auth/reset-password")
    public ResponseEntity<BaseResponse<Object>> resetPassword(@RequestHeader("4adf-3ed") String token, @RequestBody ResetPasswordRequest request) {
        return resetPasswordService.reset(token, request.getNewPassword());
    }
}
