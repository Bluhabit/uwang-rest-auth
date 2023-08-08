/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.exception;

import org.springframework.security.core.AuthenticationException;

public class UnAuthorizedException extends AuthenticationException {
    private int statusCode;
    public UnAuthorizedException(int statusCode,String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public UnAuthorizedException(String msg) {
        super(msg);
        this.statusCode = 401;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
