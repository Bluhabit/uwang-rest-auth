/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.budget.budgetCategory

import jakarta.validation.constraints.NotBlank

data class BudgetCategoryRequest(
    @field:NotBlank
    var categoryName: String?
)

data class BudgetCategoryUpdateRequest(
    @field:NotBlank
    var categoryName: String?
)