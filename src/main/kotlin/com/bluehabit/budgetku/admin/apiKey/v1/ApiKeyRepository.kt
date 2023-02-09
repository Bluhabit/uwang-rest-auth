package com.bluehabit.budgetku.admin.apiKey.v1

import org.springframework.data.repository.PagingAndSortingRepository

interface ApiKeyRepository : PagingAndSortingRepository<ApiKey, Long> {
    fun findTopByValue(value: String): ApiKey?

}