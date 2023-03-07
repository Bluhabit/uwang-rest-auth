/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.v1

import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategoryRequest
import com.bluehabit.budgetku.data.budget.BudgetService
import com.bluehabit.budgetku.data.budget.budgetCategory.BudgetCategoryUpdateRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/budget/category"]
)
class BudgetCategoryController(
    private val budgetService: BudgetService
) {
    companion object {
        const val json = "application/json"
    }

    @GetMapping(
        value = ["/list-category"],
        produces = [json]
    )
    suspend fun getListCategory(
        pageable: Pageable
    ) = budgetService.getListBudgetCategory(pageable)

    @PostMapping(
        value = ["/create-category"],
        produces = [json],
        consumes = [json]
    )
    suspend fun createNewCategory(
        @RequestBody request: BudgetCategoryRequest
    ) = budgetService.createBudgetCategory(request)

    @PutMapping(
        value = ["/update-category/{categoryId}"],
        produces = [json],
        consumes = [json]
    )
    suspend fun updateCategory(
        @PathVariable("categoryId") categoryId: String,
        @RequestBody request: BudgetCategoryUpdateRequest
    ) = budgetService.updateBudgetCategory(
        categoryId,
        request
    )

    @DeleteMapping(
        value = ["/delete-category/{categoryId}"],
        produces = [json]
    )
    suspend fun deleteCategory(
        @PathVariable("categoryId") categoryId: String
    ) = budgetService.deleteCategory(categoryId)
}