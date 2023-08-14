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
import com.bluehabit.eureka.common.JwtUtil;
import com.bluehabit.eureka.component.user.UserCredential;
import com.bluehabit.eureka.component.user.UserCredentialRepository;
import com.bluehabit.eureka.component.user.UserProfile;
import com.bluehabit.eureka.component.user.UserProfileRepository;
import com.bluehabit.eureka.component.user.UserVerificationRepository;
import com.bluehabit.eureka.component.user.model.CompleteProfileRequest;
import com.bluehabit.eureka.component.user.model.SignUpResponse;
import com.bluehabit.eureka.component.user.model.SignUpWithEmailRequest;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SignUpService extends AbstractBaseService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserVerificationRepository userVerificationRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;

    private final String keyFullName = "fullName";

    public ResponseEntity<BaseResponse<Object>> signUpWithEmail(SignUpWithEmailRequest req) {
        validate(req);
        if (userCredentialRepository.existsByEmail(req.email())) {
            throw new UnAuthorizedException(2, translate("auth.failed.user.exist"));
        }

        final String uuid = UUID.randomUUID().toString();
        final OffsetDateTime currentDate = OffsetDateTime.now();

        final UserCredential userCredential = new UserCredential();
        userCredential.setId(uuid);
        userCredential.setEmail(req.email());
        userCredential.setAuthProvider(Constant.AUTH_BASIC);
        userCredential.setActive(Constant.USER_ACTIVE);
        userCredential.setCreatedAt(currentDate);
        userCredential.setUpdatedAt(currentDate);
        userCredentialRepository.save(userCredential);

        return BaseResponse.success(translate("auth.success"), Map.of());
    }

    public ResponseEntity<BaseResponse<SignUpResponse>> completeProfile(
        CompleteProfileRequest request
    ) {
        validate(request);
        return userVerificationRepository.findByToken(request.sessionId())
            .map(userVerification -> {
                final UserProfile userProfile = new UserProfile();
                userProfile.setId(userVerification.getUser().getId());
                userProfile.setKey(keyFullName);
                userProfile.setValue(request.fullName());

                final UserProfile profile = userProfileRepository.save(userProfile);

                final UserCredential user = userVerification.getUser();
                user.setUserInfo(List.of(profile));

                final String jwtToken = jwtUtil.generateToken(user.getEmail());
                final UserCredential credential = userCredentialRepository.save(user);
                return BaseResponse.success(translate("auth.success"), new SignUpResponse(
                    jwtToken,
                    credential
                ));
            })
            .orElseThrow(() -> new UnAuthorizedException(translate("auth.session.not.valid")));
    }
}
