/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.config;

import com.bluehabit.eureka.UnAuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(
            value = UnAuthorizationException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public Map<String,String> unauthorized(UnAuthorizationException e){
        return Map.of("gehe",e.getMessage());
    }
}
