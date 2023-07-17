/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.user.model;

import com.bluehabit.eureka.component.user.UserCredential;

public record SignInResponse(
       String token,
       UserCredential credential
) {
}
