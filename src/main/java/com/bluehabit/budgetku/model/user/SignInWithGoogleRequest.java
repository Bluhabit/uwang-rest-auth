package com.bluehabit.budgetku.model.user;

import jakarta.validation.constraints.NotBlank;

public record SignInWithGoogleRequest(@NotBlank String token) { }
