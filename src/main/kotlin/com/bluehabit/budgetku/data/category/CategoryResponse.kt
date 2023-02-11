package com.bluehabit.budgetku.data.category

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.apiKey.ApiKey
import com.bluehabit.budgetku.data.apiKey.ApiKeyResponse
import com.bluehabit.budgetku.data.apiKey.toResponse
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