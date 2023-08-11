/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.controller;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.user.model.ResetPasswordRequest;
import com.bluehabit.eureka.component.user.model.SignUpWithEmailRequest;
import com.bluehabit.eureka.services.user.ResetPasswordService;
import com.bluehabit.eureka.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private ResetPasswordService resetPasswordService;

    private final String tokenResetPassword = "4adf-3ed";

    @PostMapping(
        path = "/api/v1/auth/sign-up-email",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Object>> signUpWithEmail(@NonNull @RequestBody SignUpWithEmailRequest request) {
        return userService.signUpWithEmail(request);
    }

    @PostMapping(
        path = "/api/v1/auth/reset-password"
    )
    public ResponseEntity<BaseResponse<Object>> resetPassword(
        @RequestHeader(value = tokenResetPassword, required = false) String token,
        @RequestBody ResetPasswordRequest request
    ) {
        return resetPasswordService.reset(token, request);
    }
}
