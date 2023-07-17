package com.bluehabit.budgetku.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseResponse<T> {
    private final int statusCode;
    private final String message;
    private final T data;

    private BaseResponse( int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <OUT> BaseResponse<OUT> fail(int statusCode,String message){
        return new BaseResponse<>(statusCode,message,null);
    }

    public static <OUT> ResponseEntity<BaseResponse<OUT>> success(String message, OUT data) {
        return ResponseEntity.ok(new BaseResponse<>( 200, message, data));
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
