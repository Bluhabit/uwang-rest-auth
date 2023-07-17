/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.user.model;

import jakarta.validation.constraints.NotBlank;

public record SignInWithGoogleRequest(@NotBlank String token) { }
