package com.bluehabit.budgetku.data.user

import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<User, Long> {
    @Query("SELECT case when count(m) > 0 then true else false end from User as m where m.userEmail =:userEmail")
    fun exist(
        userEmail: String,
    ):Boolean

    @Query(
        "select u from User u "
    )
    fun findAllExcludeRoleAndPermission():Page<User>
    fun findByUserEmail(userEmail: String): User?


}