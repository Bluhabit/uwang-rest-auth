/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.userActivity

import com.bluehabit.budgetku.data.apiKey.ApiKey
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserActivityRepository:PagingAndSortingRepository<UserActivity,String>, CrudRepository<UserActivity, String> {
}