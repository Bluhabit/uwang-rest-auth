package com.bluehabit.budgetku.admin.api_key


import com.bluehabit.budgetku.model.PagingDataResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime

data class ApiKeyResponse(
    val id: Long?=null,
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

fun Page<ApiKey>.toResponse() = PagingDataResponse(
    page = number,
    size=size,
    items = content.map { it.toResponse() },
    totalData = totalElements,
    totalPages = totalPages
)