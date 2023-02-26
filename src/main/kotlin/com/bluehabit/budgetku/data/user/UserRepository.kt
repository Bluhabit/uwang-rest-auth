package com.bluehabit.budgetku.data.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<User, String> {
    @Query("SELECT case when count(m) > 0 then true else false end from User as m where m.userEmail =:userEmail")
    fun exist(
        userEmail: String,
    ):Boolean

    fun findByUserEmail(userEmail: String): User?


}