package com.bluehabit.blu.services.auth;

import com.bluehabit.blu.common.AbstractBaseService;
import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.component.AuthProvider;
import com.bluehabit.blu.component.UserStatus;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import com.bluehabit.blu.component.data.useCredential.UserCredentialRepository;
import com.bluehabit.blu.component.model.SignInBasicRequest;
import com.bluehabit.blu.exception.GeneralErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignInService extends AbstractBaseService {

    @Value("${info.build.version}")
    private String version;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    public ResponseEntity<BaseResponse<UserCredential>> signInWithEmail(
            SignInBasicRequest request
    ) {
        validate(request);
        final int badRequestCode = 400;
        final var result = userCredentialRepository.findByEmail(request.email())
                .map((userCredential -> {
                    if (userCredential.getAuthProvider() != AuthProvider.BASIC) {
                        throw new GeneralErrorException(badRequestCode, translate(""));
                    }

                    if (userCredential.getStatus() != UserStatus.ACTIVE) {
                        throw new GeneralErrorException(badRequestCode, translate(""));
                    }

                    return BaseResponse.success("", userCredential, version);
                }))
                .orElseThrow(() -> new GeneralErrorException(badRequestCode, translate("")));

        return ResponseEntity.ok(result);
    }
}
