/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.blu.common;

import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public final class BaseResponse<T> {
    private final int statusCode;
    private final String message;

    private final String version;
    private final T data;

    private BaseResponse(int statusCode, String message, T data, String version) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.version = version;
    }

    public static <O> BaseResponse<O> failed(int statusCode, String message) {
        return new BaseResponse<>(statusCode, message, null, "");
    }

    public static <O> BaseResponse<O> failed(int statusCode, String message, String appVersion) {
        return new BaseResponse<>(statusCode, message, null, appVersion);
    }

    public static <O> BaseResponse<O> error(int statusCode, String message) {
        return new BaseResponse<>(statusCode, message, null, "");
    }

    public static <O> BaseResponse<O> error(int statusCode, String message, String appVersion) {
        return new BaseResponse<>(statusCode, message, null, appVersion);
    }

    public static <O> BaseResponse<O> success(String message, O data) {
        return new BaseResponse<>(HttpStatus.OK.value(), message, data, "");
    }

    public static <O> BaseResponse<O> success(String message, O data, String appVersion) {
        return new BaseResponse<>(HttpStatus.OK.value(), message, data, appVersion);
    }

    public static Map<String, Object> validationFailed(
        List<ConstraintViolation<?>> violations,
        String version
    ) {
        final int validation = 1008;
        return Map.ofEntries(
            Map.entry("statusCode", validation),
            Map.entry("data", ""),
            Map.entry("message", "Validation"),
            Map.entry("errorField", violations.stream().map(value -> Map.of(value.getPropertyPath(), value.getMessage()))),
            Map.entry("version", version)
        );
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

    public String getVersion() {
        return version;
    }
}
