/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services.user;

import com.bluehabit.eureka.common.AbstractBaseService;
import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.component.user.model.ResetPasswordRequest;
import com.bluehabit.eureka.component.user.UserCredential;
import com.bluehabit.eureka.component.user.UserCredentialRepository;
import com.bluehabit.eureka.component.user.UserVerification;
import com.bluehabit.eureka.component.user.UserVerificationRepository;
import com.bluehabit.eureka.exception.GeneralErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResetPasswordService extends AbstractBaseService {

    private UserVerificationRepository userVerificationRepository;
    private UserCredentialRepository userCredentialRepository;

    public ResponseEntity<BaseResponse<Object>> reset(String token, ResetPasswordRequest request) {
        final Optional<UserVerification> userVerification = userVerificationRepository.findByToken(token);

        if (userVerification.isEmpty()) {
            throw new GeneralErrorException(HttpStatus.NOT_FOUND.value(), translate("auth.token.invalid"));
        }
        final UserVerification userVerified = userVerification.get();

        return BaseResponse.success(translate("auth.success"), new HashMap<>());

    }
}
