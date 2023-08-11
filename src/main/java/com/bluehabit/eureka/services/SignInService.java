/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services;

import com.bluehabit.eureka.common.AbstractBaseService;
import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.Constant;
import com.bluehabit.eureka.common.GoogleAuthUtil;
import com.bluehabit.eureka.common.JwtUtil;
import com.bluehabit.eureka.component.user.UserCredential;
import com.bluehabit.eureka.component.user.UserCredentialRepository;
import com.bluehabit.eureka.component.user.model.SignInResponse;
import com.bluehabit.eureka.component.user.model.SignInWithGoogleRequest;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SignInService extends AbstractBaseService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(SignInWithGoogleRequest request) {
        validate(request);
        return GoogleAuthUtil.getGoogleClaim(request.token()).map(googleClaim -> {
            final Optional<UserCredential> userCredential = userCredentialRepository
                .findByEmail(googleClaim.email());

            if (userCredential.isEmpty()) {
                throw new UnAuthorizedException(translate("auth.user.not.exist"));
            }

            if (userCredential.get().getAuthProvider().equals(Constant.AUTH_BASIC)) {
                throw new UnAuthorizedException(translate("auth.method.not.match"));
            }

            final String jwtToken = jwtUtil.generateToken(googleClaim.email());
            return BaseResponse.success(translate("auth.success"), new SignInResponse(jwtToken, userCredential.get()));
        }).orElseThrow(() -> new UnAuthorizedException(translate("auth.token.invalid")));
    }
}
