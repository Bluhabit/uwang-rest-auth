/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.model;

import java.util.Map;

public record SignInResponse(
    String token,
    Map<String, Object> user
) {
}
