package com.bluehabit.budgetku.admin.auth.v1

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : PagingAndSortingRepository<User, Long> {
    @Query("SELECT case when count(m) > 0 then true else false end from User as m where m.email =:email")
    fun exist(
        email:String,
    ):Boolean

    fun findByEmail(email: String): User?


}