/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userCredential

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserCredentialRepository : PagingAndSortingRepository<UserCredential, String>, CrudRepository<UserCredential, String> {
    @Query("SELECT case when count(m) > 0 then true else false end from UserCredential as m where m.userEmail =:userEmail")
    fun exist(
        userEmail: String,
    ):Boolean

    fun findByUserEmail(userEmail: String): UserCredential?


}