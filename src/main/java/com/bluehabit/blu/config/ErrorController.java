/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.config;

import com.bluehabit.blu.common.BaseResponse;
import com.bluehabit.blu.exception.GeneralErrorException;
import com.bluehabit.blu.exception.UnAuthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@RestControllerAdvice
public class ErrorController {
    @Value("${info.build.version}")
    private String version;

    @Value("${info.build.version}")
    private String version;

    @Autowired
    private ApplicationContext context;

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validation(ConstraintViolationException violationException) {
        return BaseResponse.validationFailed(
            violationException.getConstraintViolations().stream().toList(),
            version
            violationException.getConstraintViolations().stream().toList(),
            context.getApplicationName()
        );
    }

    @ExceptionHandler(value = SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> signatureException(SignatureException signatureException) {
        final int signatureCode = 1_000;
        return BaseResponse.error(
            signatureCode,
            "Authentication failed, token expired or invalid",
            version
        );
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> expiredException(ExpiredJwtException expiredJwtException) {
        final int expiredJwtCode = 1_001;
        return BaseResponse.error(
            expiredJwtCode,
            expiredJwtException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> tokenNotMatchToAnyUser(UsernameNotFoundException usernameNotFoundException) {
        final int tokenBearerCode = 1_002;
        return BaseResponse.error(
            tokenBearerCode,
            usernameNotFoundException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = DecodingException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> decodeException(DecodingException decodingException) {
        final int decodeJwtFailedCode = 1_003;
        return BaseResponse.error(
            decodeJwtFailedCode,
            "Authentication failed, token expired or invalid",
            version
        );
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> nullPointer(NullPointerException nullPointerException) {
        final int nullPointerCode = 1_004;
        return BaseResponse.error(
            nullPointerCode,
            nullPointerException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> unAuth(UnAuthorizedException unAuthorizedException) {
        return BaseResponse.error(
            unAuthorizedException.getStatusCode(),
            unAuthorizedException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> contentTypeFormDataNotMatch(FileUploadException fileUploadException) {
        final int contentTypeErrorCode = 1_006;
        return BaseResponse.error(
            contentTypeErrorCode,
            fileUploadException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = GeneralErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> contentTypeFormDataNotMatch(GeneralErrorException generalErrorException) {
        return BaseResponse.error(
            generalErrorException.getStatusCode(),
            generalErrorException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> malformed(MalformedJwtException malformedJwtException) {
        return BaseResponse.error(
            HttpStatus.UNAUTHORIZED.value(),
            malformedJwtException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> noHandler(NoHandlerFoundException noHandlerFoundException) {
        return BaseResponse.error(
            HttpStatus.NOT_FOUND.value(),
            noHandlerFoundException.getMessage(),
            version
        );
    }

    @ExceptionHandler(value = UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> unSupportedException(UnsupportedOperationException noHandlerFoundException) {
        return BaseResponse.error(
            HttpStatus.NOT_FOUND.value(),
            "Something wrong",
            version
        );
    }
}
