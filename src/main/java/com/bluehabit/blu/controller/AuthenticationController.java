/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.controller;

import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import com.bluehabit.blu.services.auth.SignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(
        path = "/v1/auth"
)
@RestController
public class AuthenticationController {

    private final String tokenResetPassword = "4adf-3ed";

    @Autowired
    private SignInService signInService;

    //region sign up
    @GetMapping(
            path = "/sign-in-basic"
    )
    public ResponseEntity<BaseResponse<UserCredential>> signInBasic(){
        return signInService.signInWithEmail();
    }

}
