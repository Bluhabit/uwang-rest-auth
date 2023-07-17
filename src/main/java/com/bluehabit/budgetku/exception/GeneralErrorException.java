package com.bluehabit.budgetku.exception;

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
