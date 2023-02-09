package com.bluehabit.budgetku.data.role

import org.springframework.data.repository.PagingAndSortingRepository

interface RoleRepository : PagingAndSortingRepository<Role, String> {
    fun findByRoleName(roleName: String): Role?
}