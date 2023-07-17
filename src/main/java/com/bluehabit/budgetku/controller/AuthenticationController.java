package com.bluehabit.budgetku.controller;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.component.user.entity.UserCredential;
import com.bluehabit.budgetku.component.user.model.*;
import com.bluehabit.budgetku.component.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController()
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/auth/sign-in-email",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<SignInResponse>> signInWithEmail(@NonNull @Valid @RequestBody SignInWithEmailRequest req) {
        return userService.signInWithEmail(req);
    }

    @PostMapping(
            path = "/auth/sign-in-google",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(@NonNull @Valid @RequestBody SignInWithGoogleRequest req) {
        return userService.signInWithGoogle(req);
    }

    @PostMapping(
            path = "/auth/sign-up-email",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<UserCredential>> signUpWithEmail(@NonNull @Valid @RequestBody SignUpWithEmailRequest request) {
        return userService.signUpWithEmail(request);
    }

    @PostMapping(
            path = "/auth/sign-up-google",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<UserCredential>> signUpWithGoogle(@NonNull @Valid @RequestBody SignUpWithGoogleRequest request) {
        return userService.signUpWithGoogle(request);
    }

    @GetMapping(
            path = "/auth/refresh-token",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseResponse<String>> refreshToken(
            @RequestHeader("Authorization") String token
    ){
        return userService.refreshToken(token);
    }
}
