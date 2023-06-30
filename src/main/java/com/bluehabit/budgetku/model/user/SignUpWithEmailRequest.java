package com.bluehabit.budgetku.model.user;

import jakarta.validation.constraints.NotBlank;

public record SignUpWithEmailRequest(
        @NotBlank String email, @NotBlank String password, @NotBlank String fullName
) {
}
