package com.bluehabit.budgetku.data.apiKey

import org.springframework.data.repository.PagingAndSortingRepository

interface ApiKeyRepository : PagingAndSortingRepository<ApiKey, Long> {
    fun findTopByValue(value: String): ApiKey?

}