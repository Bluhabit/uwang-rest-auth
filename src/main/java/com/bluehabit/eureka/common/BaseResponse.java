/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class BaseResponse<T> {
    private final int statusCode;
    private final String message;
    private  T data=null;

    private BaseResponse( int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    private BaseResponse( int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static <OUT> ResponseEntity<BaseResponse<OUT>> failed(int statusCode, String message){
        return new ResponseEntity<BaseResponse<OUT>>(
                (BaseResponse<OUT>) new BaseResponse<>(statusCode,message,null),
                HttpStatus.BAD_REQUEST
        );
    }

    public static <OUT> BaseResponse<OUT> error(int statusCode, String message){
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
