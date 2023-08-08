/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.exception;

import org.springframework.security.core.AuthenticationException;

public class GeneralErrorException extends AuthenticationException {
    private int statusCode;
    public GeneralErrorException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    public GeneralErrorException(String msg) {
        super(msg);
        this.statusCode = 401;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
