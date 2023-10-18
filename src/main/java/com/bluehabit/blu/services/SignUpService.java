/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.services;

import com.bluehabit.blu.common.AbstractBaseService;
import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.common.JwtUtil;
import com.bluehabit.blu.common.MailUtil;
import com.bluehabit.blu.common.OtpGenerator;
import com.bluehabit.blu.component.AuthProvider;
import com.bluehabit.blu.component.UserStatus;
import com.bluehabit.blu.component.data.UserCredential;
import com.bluehabit.blu.component.data.UserCredentialRepository;
import com.bluehabit.blu.component.data.UserProfile;
import com.bluehabit.blu.component.data.UserProfileRepository;
import com.bluehabit.blu.component.data.UserVerification;
import com.bluehabit.blu.component.data.UserVerificationRepository;
import com.bluehabit.blu.component.model.CompleteProfileRequest;
import com.bluehabit.blu.component.model.CompleteProfileResponse;
import com.bluehabit.blu.component.model.OtpConfirmationRequest;
import com.bluehabit.blu.component.model.OtpConfirmationResponse;
import com.bluehabit.blu.component.model.SignUpWithEmailRequest;
import com.bluehabit.blu.component.model.SignUpWithEmailResponse;
import com.bluehabit.blu.component.VerificationType;
import com.bluehabit.blu.exception.GeneralErrorException;
import com.bluehabit.blu.exception.UnAuthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    @Autowired
    private OtpGenerator otpGenerator;
    @Autowired
    private MailUtil mailUtil;

    private final String keyFullName = "fullName";
    //region sign up

    public ResponseEntity<BaseResponse<Object>> signUpWithEmail(SignUpWithEmailRequest req) {
        validate(req);
        if (userCredentialRepository.existsByEmail(req.email())) {
            throw new UnAuthorizedException(2, translate("auth.sign_up.email.exist"));
        }

        final String uuid = UUID.randomUUID().toString();
        final OffsetDateTime currentDate = OffsetDateTime.now();

        final UserCredential userCredential = new UserCredential();
        userCredential.setId(uuid);
        userCredential.setEmail(req.email());
        userCredential.setAuthProvider(AuthProvider.BASIC);
        userCredential.setStatus(UserStatus.INACTIVE);
        userCredential.setCreatedAt(currentDate);
        userCredential.setUpdatedAt(currentDate);

        final UserCredential userCredentialSaved = userCredentialRepository.save(userCredential);

        final String otpGen = OtpGenerator.generateOtp();

        final UserVerification otp = new UserVerification();
        otp.setToken(otpGen);
        otp.setUser(userCredentialSaved);
        otp.setType(VerificationType.OTP);
        otp.setCreatedAt(currentDate);
        otp.setUpdatedAt(currentDate);

        userVerificationRepository.save(otp);

        mailUtil.sendEmail(
            userCredential.getEmail(),
            translate("auth.send_otp.subject"),
            "otp",
            Map.of(
                "name", "Gawean User",
                "otp", otpGen
            )
        );

        return BaseResponse.success(
            translate("auth.success"),
            new SignUpWithEmailResponse(otp.getId())
        );
    }

    public ResponseEntity<BaseResponse<OtpConfirmationResponse>> otpConfirmation(OtpConfirmationRequest req) {
        validate(req);
        return userVerificationRepository.findById(req.sessionId()).map(userVerification -> {
                if (!userVerification.getToken().equals(req.otp())) {
                    throw new GeneralErrorException(HttpStatus.BAD_REQUEST.value(), translate("auth.sign_up.otp.failed"));
                }
                return BaseResponse.success(
                    translate("auth.sign_up.otp.success"),
                    new OtpConfirmationResponse(
                        userVerification
                            .getId()
                    )
                );
            })
            .orElseThrow(() -> new GeneralErrorException(HttpStatus.NOT_FOUND.value(), translate("auth.sign_up.otp.failed")));
    }

    public ResponseEntity<BaseResponse<CompleteProfileResponse>> completeProfile(
        CompleteProfileRequest request
    ) {
        validate(request);
        return userVerificationRepository.findById(request.sessionId())
            .map(userVerification -> {
                final OffsetDateTime currentDate = OffsetDateTime.now();
                final UserProfile userProfile = new UserProfile();
                userProfile.setId(userVerification.getUser().getId());
                userProfile.setKey(keyFullName);
                userProfile.setValue(request.fullName());
                userProfile.setUserId(userVerification.getUser().getId());
                userProfile.setUpdatedAt(currentDate);
                userProfile.setCreatedAt(currentDate);

                final UserProfile profile = userProfileRepository.save(userProfile);
                final String newPassword = encoder.encode(request.password());

                final UserCredential user = userVerification.getUser();
                final List<UserProfile> profileList = new ArrayList<>();
                profileList.add(profile);
                user.setUserInfo(profileList);
                user.setPassword(newPassword);
                user.setUpdatedAt(currentDate);

                final String jwtToken = jwtUtil.generateToken(user.getEmail());
                final UserCredential credential = userCredentialRepository.save(user);
                userVerificationRepository.deleteById(request.sessionId());
                return BaseResponse.success(
                    translate("auth.success"),
                    new CompleteProfileResponse(
                        jwtToken,
                        credential.toResponse()
                    )
                );
            })
            .orElseThrow(() -> new UnAuthorizedException(translate("auth.session.not.valid")));
    }
    //end region
}
