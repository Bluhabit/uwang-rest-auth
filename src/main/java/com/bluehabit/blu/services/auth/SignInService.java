package com.bluehabit.blu.services.auth;

import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.component.data.useCredential.UserCredential;
import com.bluehabit.blu.component.data.useCredential.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignInService {
    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Value("${info.build.version}")
    private String version;

    public ResponseEntity<BaseResponse<UserCredential>> signInWithEmail() {
        return BaseResponse.success("", null, version);
    }
}
