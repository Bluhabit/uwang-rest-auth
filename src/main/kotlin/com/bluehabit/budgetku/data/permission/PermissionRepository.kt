/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.permission

import org.springframework.data.repository.PagingAndSortingRepository

interface PermissionRepository:PagingAndSortingRepository<Permission,String> {
}