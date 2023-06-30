package com.bluehabit.budgetku.config;

import com.bluehabit.budgetku.common.BaseResponse;
import com.bluehabit.budgetku.common.Constant;
import com.bluehabit.budgetku.exception.UnAuthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = SignatureException.class)
    public ResponseEntity<BaseResponse<String>> signatureException(SignatureException e) {
        return BaseResponse.unAuthorized(
                Constant.BKA_1000, "Authentication failed, token expired or invalid"
        );
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<BaseResponse<String>> expiredException(ExpiredJwtException e) {
        return BaseResponse.unAuthorized(
                Constant.BKA_1001, e.getMessage()
        );
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> tokenNotMatchToAnyUser(UsernameNotFoundException e) {
        return BaseResponse.unAuthorized(
                Constant.BKA_1002, e.getMessage()
        );
    }

    @ExceptionHandler(value = DecodingException.class)
    public ResponseEntity<BaseResponse<String>> decodeException(DecodingException e) {
        return BaseResponse.unAuthorized(
                Constant.BKA_1003, "Authentication failed, token expired or invalid"
        );
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<BaseResponse<String>> nullPointer(NullPointerException e) {
        return BaseResponse.error(
                Constant.BKA_1004, e.getMessage()
        );
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    public ResponseEntity<BaseResponse<String>> unAuth(UnAuthorizedException e) {
        return BaseResponse.unAuthorized(
                Constant.BKA_1005, e.getMessage()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<String>> validation(MethodArgumentNotValidException e) {
        return BaseResponse.error(
                Constant.BKA_1006, e.getMessage()
        );
    }
}
