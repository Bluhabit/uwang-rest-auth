/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.config;

import com.bluehabit.eureka.common.BaseResponse;
import com.bluehabit.eureka.common.Constant;
import com.bluehabit.eureka.exception.GeneralErrorException;
import com.bluehabit.eureka.exception.UnAuthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validation(ConstraintViolationException violationException) {
        return BaseResponse.validationFailed(
            violationException.getConstraintViolations().stream().toList()
        );
    }

    @ExceptionHandler(value = SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> signatureException(SignatureException signatureException) {
        return BaseResponse.error(
            Constant.BKA_1000, "Authentication failed, token expired or invalid"
        );
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> expiredException(ExpiredJwtException expiredJwtException) {
        return BaseResponse.error(Constant.BKA_1001_EXPIRED, expiredJwtException.getMessage());
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> tokenNotMatchToAnyUser(UsernameNotFoundException usernameNotFoundException) {
        return BaseResponse.error(
            Constant.BKA_1002, usernameNotFoundException.getMessage()
        );
    }

    @ExceptionHandler(value = DecodingException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> decodeException(DecodingException decodingException) {
        return BaseResponse.error(
            Constant.BKA_1003, "Authentication failed, token expired or invalid"
        );
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> nullPointer(NullPointerException nullPointerException) {
        return BaseResponse.error(
            Constant.BKA_1004, nullPointerException.getMessage()
        );
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> unAuth(UnAuthorizedException unAuthorizedException) {
        return BaseResponse.error(
            unAuthorizedException.getStatusCode(), unAuthorizedException.getMessage()
        );
    }

    @ExceptionHandler(value = FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> contentTypeFormDataNotMatch(FileUploadException fileUploadException) {
        return BaseResponse.error(
            Constant.BKA_1006, fileUploadException.getMessage()
        );
    }

    @ExceptionHandler(value = GeneralErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> contentTypeFormDataNotMatch(GeneralErrorException generalErrorException) {
        return BaseResponse.error(
            generalErrorException.getStatusCode(), generalErrorException.getMessage()
        );
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<String> malformed(MalformedJwtException malformedJwtException) {
        return BaseResponse.error(
            HttpStatus.UNAUTHORIZED.value(), malformedJwtException.getMessage()
        );
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> noHandler(NoHandlerFoundException noHandlerFoundException) {
        return BaseResponse.error(
            HttpStatus.NOT_FOUND.value(),
            noHandlerFoundException.getMessage()
        );
    }
}
