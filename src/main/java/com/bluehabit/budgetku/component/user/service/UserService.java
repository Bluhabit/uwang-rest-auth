/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.user.service;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.common.GoogleAuthUtil;
import com.bluehabit.budgetku.component.user.model.*;
import com.bluehabit.budgetku.config.JwtService;
import com.bluehabit.budgetku.component.user.entity.UserCredential;
import com.bluehabit.budgetku.component.user.entity.UserProfile;
import com.bluehabit.budgetku.exception.UnAuthorizedException;
import com.bluehabit.budgetku.component.user.repo.UserCredentialRepository;
import com.bluehabit.budgetku.component.user.repo.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtService jwtService;


    public ResponseEntity<BaseResponse<SignInResponse>> signInWithEmail(SignInWithEmailRequest request) {
        var user = userCredentialRepository.findByUserEmail(request.email());

        if (user.isEmpty()) {
            throw new UnAuthorizedException("Sign in failed, user not found!");
        }
        if (!encoder.matches(request.password(), user.get().getUserPassword())) {
            throw new UnAuthorizedException("Email or Password invalid");
        }
        if (!user.get().getUserAuthProvider().equals("BASIC")) {
            throw new UnAuthorizedException("Email already registered to another account");
        }

        var token = jwtService.generateToken(user.get().getUserEmail());
        return BaseResponse.success("Sign in with email success", new SignInResponse(token, user.get()));
    }

    public ResponseEntity<BaseResponse<SignInResponse>> signInWithGoogle(SignInWithGoogleRequest request) {
        var googleAuthClaim = GoogleAuthUtil.getGoogleClaim(request.token());

        if (googleAuthClaim.isEmpty()) {
            throw new UnAuthorizedException("Given token is invalid");
        }

        var findUser = userCredentialRepository.findByUserEmail(googleAuthClaim.get().email());
        if (findUser.isEmpty()) {
            throw new UnAuthorizedException("User not registered!");
        }

        if (!findUser.get().getUserAuthProvider().equals("GOOGLE")) {
            throw new UnAuthorizedException("Email already registered to another account");
        }

        var token = jwtService.generateToken(findUser.get().getUserEmail());
        return BaseResponse.success("Sign in with email success", new SignInResponse(token, findUser.get()));
    }

    public ResponseEntity<BaseResponse<UserCredential>> signUpWithGoogle(SignUpWithGoogleRequest req) {
        var claims = GoogleAuthUtil.getGoogleClaim(req.token());
        if (claims.isEmpty()) {
            throw  new UnAuthorizedException(1, "Token is invalid");
        }

        var findUser = userCredentialRepository.exist(claims.get().email());
        if (findUser) {
            throw  new UnAuthorizedException(2, "Already registered");
        }
        var uuid = UUID.randomUUID().toString();
        var currentDate = OffsetDateTime.now();

        var userProfile = new UserProfile();
        userProfile.setUserId(uuid);
        userProfile.setUserFullName(claims.get().fullName());
        userProfile.setUserProfilePicture(null);
        userProfile.setUserDateOfBirth(null);
        userProfile.setUserCountryCode(claims.get().locale());
        userProfile.setUserPhoneUmber(null);
        userProfile.setCreatedAt(currentDate);
        userProfile.setUpdatedAt(currentDate);

        var savedProfile = userProfileRepository.save(userProfile);
        var userCredential = new UserCredential();
        userCredential.setUserId(uuid);
        userCredential.setUserEmail(claims.get().email());
        userCredential.setUserPassword(encoder.encode(claims.get().email()));
        userCredential.setUserStatus("ACTIVE");
        userCredential.setUserAuthProvider("GOOGLE");
        userCredential.setUserProfile(savedProfile);
        userCredential.setUserNotificationToken("");
        userCredential.setCreatedAt(currentDate);
        userCredential.setUpdatedAt(currentDate);
        var savedCredential = userCredentialRepository.save(userCredential);


        return BaseResponse.success("Success", savedCredential);
    }

    public ResponseEntity<BaseResponse<UserCredential>> signUpWithEmail(SignUpWithEmailRequest req) {

        var findUser = userCredentialRepository.exist(req.email());
        if (findUser) {
            throw new UnAuthorizedException(2, "Already registered");
        }
        var uuid = UUID.randomUUID().toString();
        var currentDate = OffsetDateTime.now();

        var userProfile = new UserProfile();
        userProfile.setUserId(uuid);
        userProfile.setUserFullName(req.fullName());
        userProfile.setUserProfilePicture(null);
        userProfile.setUserDateOfBirth(null);
        userProfile.setUserCountryCode("ID");
        userProfile.setUserPhoneUmber(null);
        userProfile.setCreatedAt(currentDate);
        userProfile.setUpdatedAt(currentDate);

        var savedProfile = userProfileRepository.save(userProfile);
        var userCredential = new UserCredential();
        userCredential.setUserId(uuid);
        userCredential.setUserEmail(req.email());
        userCredential.setUserPassword(encoder.encode(req.password()));
        userCredential.setUserStatus("ACTIVE");
        userCredential.setUserAuthProvider("GOOGLE");
        userCredential.setUserProfile(savedProfile);
        userCredential.setUserNotificationToken("");
        userCredential.setCreatedAt(currentDate);
        userCredential.setUpdatedAt(currentDate);
        var savedCredential = userCredentialRepository.save(userCredential);


        return BaseResponse.success("Success", savedCredential);
    }

    public ResponseEntity<BaseResponse<List<UserProfile>>> getUsers(Pageable pageable) {
        var user = userProfileRepository.findAll(pageable);
        return BaseResponse.success("Get all users", user.toList());
    }

    public ResponseEntity<BaseResponse<String>> refreshToken(String token){
        try{
            if (token == null || token.isEmpty()) {
                throw  new UnAuthorizedException(401,"Token not provided");
            }

            if (!token.startsWith("Bearer")) {
                throw  new UnAuthorizedException(401,"header doesn't contain Bearer");
            }

            String split = token.substring(7);
            String username = jwtService.extractFromExpired(split);

            if (username.isEmpty()) {
                throw  new UnAuthorizedException(401,"failed extract claim");
            }
            var findUser = userCredentialRepository.findByUserEmail(username);

            if(findUser.isEmpty()){
                throw  new UnAuthorizedException(4000,"there is no user with "+username);
            }

            var generatedToken = jwtService.generateToken(findUser.get().getUserEmail());

            return BaseResponse.success("Success",generatedToken);
        }catch (Exception e){
            throw  new UnAuthorizedException(401,e.getClass().getSimpleName());
        }
    }

}
