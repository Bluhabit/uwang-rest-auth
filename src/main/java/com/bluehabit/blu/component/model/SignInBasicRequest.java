package com.bluehabit.blu.component.model;

import jakarta.validation.constraints.NotBlank;

public record SignInBasicRequest(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
