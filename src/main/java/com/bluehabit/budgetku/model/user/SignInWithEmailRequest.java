package com.bluehabit.budgetku.model.user;

import jakarta.validation.constraints.NotBlank;

public record SignInWithEmailRequest(
        @NotBlank String email, @NotBlank String password
) { }
