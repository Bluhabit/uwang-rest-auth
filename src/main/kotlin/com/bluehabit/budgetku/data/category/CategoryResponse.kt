/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.category

import com.bluehabit.budgetku.common.utils.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page

data class CategoryResponse(
    var categoryId: String? = null,
    var categoryName: String,
    var categorySlug: String,
    var createdAt: String? = null,
    var updatedAt: String? = null
)


fun Category.toResponse() = CategoryResponse(
    categoryId = categoryId,
    categoryName = categoryName,
    categorySlug = categorySlug,
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)

fun Page<Category>.toResponse() = pagingResponse<CategoryResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}