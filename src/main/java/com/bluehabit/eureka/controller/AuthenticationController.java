/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.controller;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.user.model.OtpConfirmationRequest;
import com.bluehabit.eureka.component.user.model.OtpConfirmationResponse;
import com.bluehabit.eureka.component.user.model.CompleteProfileRequest;
import com.bluehabit.eureka.component.user.model.ResetPasswordRequest;
import com.bluehabit.eureka.component.user.model.SignInResponse;
import com.bluehabit.eureka.component.user.model.SignInWithGoogleRequest;
import com.bluehabit.eureka.component.user.model.SignUpResponse;
import com.bluehabit.eureka.component.user.model.SignUpWithEmailRequest;
import com.bluehabit.eureka.services.SignInService;
import com.bluehabit.eureka.services.SignUpService;
import com.bluehabit.eureka.services.ResetPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SignInService signInService;

    @Autowired
    private ResetPasswordService resetPasswordService;

    private final String tokenResetPassword = "4adf-3ed";

    //region sign up

    @PostMapping(
        path = "/api/v1/auth/sign-up-email",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Object>> signUpWithEmail(
        @RequestBody SignUpWithEmailRequest request
    ) {
        return signUpService.signUpWithEmail(request);
    }

    @PostMapping(
        path = "/api/v1/auth/otp-confirmation"
    )
    public ResponseEntity<BaseResponse<OtpConfirmationResponse>> otpConfirmation(
        @RequestBody OtpConfirmationRequest request
    ) {
        return signUpService.otpConfirmation(request);
    }

    @PostMapping(
        path = "/api/v1/auth/complete-profile",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<SignUpResponse>> completeProfile(
        @RequestBody CompleteProfileRequest request
    ) {
        return signUpService.completeProfile(request);
    }

    //end region
    //region sign in

    @PostMapping(
        path = "/api/v1/auth/sign-in-google",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(
        @RequestBody SignInWithGoogleRequest request
    ) {
        return signInService.signInWithGoogle(request);
    }

    //end region
    //region reset password
    @PostMapping(
        path = "/api/v1/auth/reset-password"
    )
    public ResponseEntity<BaseResponse<Object>> resetPassword(
        @RequestHeader(value = tokenResetPassword, required = false) String token,
        @RequestBody ResetPasswordRequest request
    ) {
        return resetPasswordService.resetPassword(token, request);
    }
    //end region
}
