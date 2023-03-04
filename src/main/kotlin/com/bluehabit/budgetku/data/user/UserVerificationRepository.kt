/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.data.apiKey.ApiKey
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserVerificationRepository : PagingAndSortingRepository<UserVerification, String>,
    CrudRepository<UserVerification, String> {
    @Query("SELECT case when count(m) > 0 then true else false end from UserActivation as m where m.userActivationStatusToken =:userActivationToken")
    fun exist(
        userActivationToken: String,
    ):Boolean

    fun findByUserActivationToken(userActivationToken: String): UserVerification?


}