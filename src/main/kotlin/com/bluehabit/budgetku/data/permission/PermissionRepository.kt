package com.bluehabit.budgetku.data.permission

import org.springframework.data.repository.PagingAndSortingRepository

interface PermissionRepository:PagingAndSortingRepository<Permission,String> {
}