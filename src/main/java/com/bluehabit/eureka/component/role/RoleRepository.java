/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.eureka.component.role;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RoleRepository extends PagingAndSortingRepository<Role,String>, CrudRepository<Role,String> {
    @Query("select p from Role p where p.roleName like ?1")
    List<Role> searchByName(String roleName);
    boolean existsByIdIgnoreCase(String id);
}
