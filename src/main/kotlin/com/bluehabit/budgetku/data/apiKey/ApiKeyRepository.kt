/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.apiKey

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface ApiKeyRepository : PagingAndSortingRepository<ApiKey, String>,CrudRepository<ApiKey,String> {
    fun findTopByValue(value: String): ApiKey?

}