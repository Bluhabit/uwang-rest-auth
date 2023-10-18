/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ValidationUtil {
    @Autowired
    private Validator validator;

    @Autowired
    private ResourceBundleMessageSource i18in;

    public void validate(Object value) {
        final Set<ConstraintViolation<Object>> result = validator.validate(value);
        if (result.size() != 0) {
            throw new ConstraintViolationException(result);
        }
    }
}
