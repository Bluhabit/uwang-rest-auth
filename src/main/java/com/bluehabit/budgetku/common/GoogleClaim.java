/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common;

public record GoogleClaim(
    String email,
    String picture,
    String fullName,
    String locale,
    String message
){

}