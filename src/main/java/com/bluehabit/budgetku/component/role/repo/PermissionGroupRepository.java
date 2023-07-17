/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.component.role.repo;

import com.bluehabit.budgetku.component.role.entity.PermissionGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PermissionGroupRepository extends PagingAndSortingRepository<PermissionGroup,String>, CrudRepository<PermissionGroup,String> {
    @Query("select p from PermissionGroup p where p.roleName like ?1")
    List<PermissionGroup> searchByName(String roleName);
    boolean existsByRoleIdIgnoreCase(String roleId);
}
