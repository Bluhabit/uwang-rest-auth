package com.bluehabit.budgetku.exception;

import org.springframework.security.core.AuthenticationException;

public class UnAuthorizedException extends AuthenticationException {
    public UnAuthorizedException(String msg) {
        super(msg);
    }
}
