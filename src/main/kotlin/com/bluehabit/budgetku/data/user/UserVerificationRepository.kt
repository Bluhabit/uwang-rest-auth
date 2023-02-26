package com.bluehabit.budgetku.data.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

interface UserVerificationRepository : PagingAndSortingRepository<UserVerification, String> {
    @Query("SELECT case when count(m) > 0 then true else false end from UserActivation as m where m.userActivationStatusToken =:userActivationToken")
    fun exist(
        userActivationToken: String,
    ):Boolean

    fun findByUserActivationToken(userActivationToken: String): UserVerification?


}