/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.role.model;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteRolesRequest(
        @NotEmpty List<String> ids
) { }
