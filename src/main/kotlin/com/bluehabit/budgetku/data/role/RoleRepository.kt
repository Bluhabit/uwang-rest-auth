/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role

import org.springframework.data.repository.PagingAndSortingRepository

interface RoleRepository : PagingAndSortingRepository<Role, String> {
    fun findByRoleName(roleName: String): Role?
}