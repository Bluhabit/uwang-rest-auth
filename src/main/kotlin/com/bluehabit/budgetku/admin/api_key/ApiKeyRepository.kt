package com.bluehabit.budgetku.admin.api_key

import org.springframework.data.repository.PagingAndSortingRepository

interface ApiKeyRepository : PagingAndSortingRepository<ApiKey, Long> {
    fun findTopByValue(value: String): ApiKey?

}