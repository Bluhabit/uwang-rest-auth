package com.bluehabit.budgetku.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponse<T> {
    private int statusCode;
    private String message;
    private T data;

    private BaseResponse( int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <OUT> ResponseEntity<BaseResponse<OUT>> success(String message, OUT data) {
        return ResponseEntity.ok(new BaseResponse<>( 200, message, data));
    }
    public static <OUT> ResponseEntity<BaseResponse<OUT>> error(int statusCode, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>( statusCode, message, null));
    }

    public static <OUT> ResponseEntity<BaseResponse<OUT>> unAuthorized(int statusCode, String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>( statusCode, message, null));
    }

    public static <OUT> ResponseEntity<BaseResponse<OUT>> failed(int statusCode, String message) {
        return ResponseEntity.ok(new BaseResponse<>(statusCode, message, null));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
