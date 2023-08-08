/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.services.user;

import com.bluehabit.eureka.common.AbstractBaseService;
import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.GoogleAuthUtil;
import com.bluehabit.eureka.common.JwtUtil;
import com.bluehabit.eureka.component.user.UserCredential;
import com.bluehabit.eureka.component.user.UserCredentialRepository;
import com.bluehabit.eureka.component.user.UserProfile;
import com.bluehabit.eureka.component.user.UserProfileRepository;
import com.bluehabit.eureka.component.user.model.SignInResponse;
import com.bluehabit.eureka.component.user.model.SignInWithEmailRequest;
import com.bluehabit.eureka.component.user.model.SignInWithGoogleRequest;
import com.bluehabit.eureka.component.user.model.SignUpWithEmailRequest;
import com.bluehabit.eureka.component.user.model.SignUpWithGoogleRequest;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    public ResponseEntity<BaseResponse<SignInResponse>> signInWithEmail(SignInWithEmailRequest request) {
        validate(request);
        return userCredentialRepository.findByUserEmail(request.email()).map(user -> {
            if (!encoder.matches(request.password(), user.getUserPassword())) {
                throw new UnAuthorizedException("Email or Password invalid");
            }
            if (!user.getUserAuthProvider().equals("BASIC")) {
                throw new UnAuthorizedException("Email already registered to another account");
            }

            final String token = jwtUtil.generateToken(user.getUserEmail());
            return BaseResponse.success("Sign in with email success", new SignInResponse(token, user));

        }).orElseThrow(() -> new UnAuthorizedException("Sign in failed, user not found!"));
    }

    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(SignInWithGoogleRequest request) {
        return GoogleAuthUtil.getGoogleClaim(request.token()).map(googleClaim -> {
            final Optional<UserCredential> findUser = userCredentialRepository.findByUserEmail(googleClaim.email());
            if (findUser.isEmpty()) {
                throw new UnAuthorizedException("User not registered!");
            }

            if (!findUser.get().getUserAuthProvider().equals("GOOGLE")) {
                throw new UnAuthorizedException("Email already registered to another account");
            }

            final String token = jwtUtil.generateToken(findUser.get().getUserEmail());
            return BaseResponse.success("Sign in with email success", new SignInResponse(token, findUser.get()));
        }).orElseThrow(() -> new UnAuthorizedException("Given token is invalid"));
    }

    public ResponseEntity<BaseResponse<UserCredential>> signUpWithGoogle(SignUpWithGoogleRequest req) {
        return GoogleAuthUtil.getGoogleClaim(req.token()).map(claims -> {
                final Optional<UserCredential> findUser = userCredentialRepository.findByUserEmail(claims.email());
                if (findUser.isEmpty()) {
                    throw new UnAuthorizedException(2, "Already registered");
                }
                final String uuid = UUID.randomUUID().toString();
                final OffsetDateTime currentDate = OffsetDateTime.now();

                final UserProfile userProfile = new UserProfile();
                userProfile.setUserId(uuid);
                userProfile.setUserFullName(claims.fullName());
                userProfile.setUserProfilePicture(null);
                userProfile.setUserDateOfBirth(null);
                userProfile.setUserCountryCode(claims.locale());
                userProfile.setUserPhoneUmber(null);
                userProfile.setCreatedAt(currentDate);
                userProfile.setUpdatedAt(currentDate);

                final UserProfile savedProfile = userProfileRepository.save(userProfile);
                final UserCredential userCredential = new UserCredential();
                userCredential.setUserId(uuid);
                userCredential.setUserEmail(claims.email());
                userCredential.setUserPassword(encoder.encode(claims.email()));
                userCredential.setUserStatus("ACTIVE");
                userCredential.setUserAuthProvider("GOOGLE");
                userCredential.setUserProfile(savedProfile);
                userCredential.setUserNotificationToken("");
                userCredential.setCreatedAt(currentDate);
                userCredential.setUpdatedAt(currentDate);
                final UserCredential savedCredential = userCredentialRepository.save(userCredential);

                return BaseResponse.success("Success", savedCredential);
            })
            .orElseThrow(() -> new UnAuthorizedException(1, "Token is invalid"));
    }

    public ResponseEntity<BaseResponse<UserCredential>> signUpWithEmail(SignUpWithEmailRequest req) {
        return userCredentialRepository.findByUserEmail(req.email()).map(user -> {
            final String uuid = UUID.randomUUID().toString();
            final OffsetDateTime currentDate = OffsetDateTime.now();

            final UserProfile userProfile = new UserProfile();
            userProfile.setUserId(uuid);
            userProfile.setUserFullName(req.fullName());
            userProfile.setUserProfilePicture(null);
            userProfile.setUserDateOfBirth(null);
            userProfile.setUserCountryCode("ID");
            userProfile.setUserPhoneUmber(null);
            userProfile.setCreatedAt(currentDate);
            userProfile.setUpdatedAt(currentDate);

            final UserProfile savedProfile = userProfileRepository.save(userProfile);
            final UserCredential userCredential = new UserCredential();
            userCredential.setUserId(uuid);
            userCredential.setUserEmail(req.email());
            userCredential.setUserPassword(encoder.encode(req.password()));
            userCredential.setUserStatus("ACTIVE");
            userCredential.setUserAuthProvider("GOOGLE");
            userCredential.setUserProfile(savedProfile);
            userCredential.setUserNotificationToken("");
            userCredential.setCreatedAt(currentDate);
            userCredential.setUpdatedAt(currentDate);
            final UserCredential savedCredential = userCredentialRepository.save(userCredential);

            return BaseResponse.success("Success", savedCredential);
        }).orElseThrow(() -> new UnAuthorizedException(2, "Already registered"));
    }

    public ResponseEntity<BaseResponse<List<UserProfile>>> getUsers(Pageable pageable) {
        final Page<UserProfile> user = userProfileRepository.findAll(pageable);
        return BaseResponse.success("Get all users", user.toList());
    }

    public ResponseEntity<BaseResponse<String>> refreshToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), "Token not provided");
        }

        if (!token.startsWith("Bearer")) {
            throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), "header doesn't contain Bearer");
        }

        final String split = token.substring(7);
        final String username = jwtUtil.extractFromExpired(split);

        if (username.isEmpty()) {
            throw new UnAuthorizedException(HttpStatus.UNAUTHORIZED.value(), "failed extract claim");
        }
        final Optional<UserCredential> findUser = userCredentialRepository.findByUserEmail(username);

        if (findUser.isEmpty()) {
            throw new UnAuthorizedException(HttpStatus.FORBIDDEN.value(), "there is no user with " + username);
        }

        final String generatedToken = jwtUtil.generateToken(findUser.get().getUserEmail());

        return BaseResponse.success("Success", generatedToken);
    }

}
