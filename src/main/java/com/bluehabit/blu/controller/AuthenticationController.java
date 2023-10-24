/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.controller;

import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.component.model.CompleteProfileRequest;
import com.bluehabit.blu.component.model.LinkResetPasswordConfirmationRequest;
import com.bluehabit.blu.component.model.LinkResetPasswordConfirmationResponse;
import com.bluehabit.blu.component.model.OtpConfirmationRequest;
import com.bluehabit.blu.component.model.OtpConfirmationResponse;
import com.bluehabit.blu.component.model.RequestResetPasswordRequest;
import com.bluehabit.blu.component.model.ResetPasswordRequest;
import com.bluehabit.blu.component.model.SignInResponse;
import com.bluehabit.blu.component.model.SignInWithEmailRequest;
import com.bluehabit.blu.component.model.SignInWithGoogleRequest;
import com.bluehabit.blu.component.model.CompleteProfileResponse;
import com.bluehabit.blu.component.model.SignUpWithEmailRequest;
import com.bluehabit.blu.services.ResetPasswordService;
import com.bluehabit.blu.services.SignInService;
import com.bluehabit.blu.services.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        path = "/api/v1/auth/otp-confirmation",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
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
    public ResponseEntity<BaseResponse<CompleteProfileResponse>> completeProfile(
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
        path = "/api/v1/auth/request-reset-password",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Map<Object, Object>>> requestResetPassword(
        @RequestBody RequestResetPasswordRequest request
    ) {
        return resetPasswordService.requestResetPassword(request);
    }

    @PostMapping(
        path = "/api/v1/auth/link-confirmation",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<LinkResetPasswordConfirmationResponse>> linkResetPasswordConfirmation(
        @RequestBody LinkResetPasswordConfirmationRequest request
    ) {
        return resetPasswordService.linkConfirmation(request);
    }

    @PostMapping(
        path = "/api/v1/auth/reset-password",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<Map<Object, Object>>> resetPassword(
        @RequestHeader(value = tokenResetPassword, required = false) String token,
        @RequestBody ResetPasswordRequest request
    ) {
        return resetPasswordService.setNewPassword(token, request);
    }
    //end region

    @PostMapping(
        path = "/api/v1/auth/sign-in",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<SignInResponse>> signIn(@RequestBody SignInWithEmailRequest request) {
        return signInService.signIn(request);
    }

}
