/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services.user;

import com.bluehabit.eureka.common.AbstractBaseService;
import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.Constant;
import com.bluehabit.eureka.common.JwtUtil;
import com.bluehabit.eureka.component.user.UserCredential;
import com.bluehabit.eureka.component.user.UserCredentialRepository;
import com.bluehabit.eureka.component.user.UserProfile;
import com.bluehabit.eureka.component.user.UserProfileRepository;
import com.bluehabit.eureka.component.user.model.SignUpWithEmailRequest;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends AbstractBaseService {
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<BaseResponse<Object>> signUpWithEmail(SignUpWithEmailRequest req) {
        if (userCredentialRepository.existsByEmail(req.email())) {
            throw new UnAuthorizedException(2, "Email already exist");
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

        return BaseResponse.success("Success", new Object());
    }

    public ResponseEntity<BaseResponse<List<UserProfile>>> getUsers(Pageable pageable) {
        final Page<UserProfile> user = userProfileRepository.findAll(pageable);
        return BaseResponse.success("Get all users", user.toList());
    }
}
