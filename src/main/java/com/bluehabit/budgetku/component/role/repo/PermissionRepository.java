/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.role.repo;

import com.bluehabit.budgetku.component.role.entity.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, String>, CrudRepository<Permission, String> {
}
