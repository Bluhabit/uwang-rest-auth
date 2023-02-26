/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.apiKey


import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime

data class ApiKeyResponse(
    val id: String?=null,
    val apiKey: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

fun ApiKey.toResponse() = ApiKeyResponse(
    id,
    value,
    createdAt,
    updatedAt
)

fun Page<ApiKey>.toResponse() = pagingResponse<ApiKeyResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}