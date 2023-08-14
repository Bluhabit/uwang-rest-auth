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

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SignInService extends AbstractBaseService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(SignInWithGoogleRequest request) {
        validate(request);
        return GoogleAuthUtil.getGoogleClaim(request.token()).map(googleClaim -> {
                final String jwtToken = jwtUtil.generateToken(googleClaim.email());
                return userCredentialRepository
                    .findByEmail(googleClaim.email()).map(user -> {
                        if (!user.getAuthProvider().equals(Constant.AUTH_GOOGLE)) {
                            throw new UnAuthorizedException(translate("auth.method.provider.not.match"));
                        }

                        return BaseResponse.success(
                            translate("auth.success"),
                            new SignInResponse(jwtToken, user)
                        );
                    }).orElseGet(() -> {
                        final String uuid = UUID.randomUUID().toString();
                        final OffsetDateTime currentDate = OffsetDateTime.now();

                        final UserCredential userCredential = new UserCredential();
                        userCredential.setId(uuid);
                        userCredential.setEmail(googleClaim.email());
                        userCredential.setAuthProvider(Constant.AUTH_GOOGLE);
                        userCredential.setActive(Constant.USER_ACTIVE);
                        userCredential.setCreatedAt(currentDate);
                        userCredential.setUpdatedAt(currentDate);
                        final UserCredential saved = userCredentialRepository.save(userCredential);

                        return BaseResponse.success(
                            translate("auth.success"),
                            new SignInResponse(jwtToken, saved)
                        );
                    });
            }
        ).orElseThrow(() -> new UnAuthorizedException(translate("auth.token.invalid")));
    }
}
