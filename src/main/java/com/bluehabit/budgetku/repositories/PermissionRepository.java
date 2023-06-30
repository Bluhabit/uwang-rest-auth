package com.bluehabit.budgetku.repositories;

import com.bluehabit.budgetku.entity.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, String>, CrudRepository<Permission, String> {
}
