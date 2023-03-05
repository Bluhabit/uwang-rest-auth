/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.category

import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime

data class CategoryResponse(
    var categoryId: String? = null,
    var categoryName: String,
    var categorySlug: String,
    var createdAt: OffsetDateTime?,
    var updatedAt: OffsetDateTime?
)


fun Category.toResponse() = CategoryResponse(
    categoryId = categoryId,
    categoryName = categoryName,
    categorySlug = categorySlug,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Page<Category>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}