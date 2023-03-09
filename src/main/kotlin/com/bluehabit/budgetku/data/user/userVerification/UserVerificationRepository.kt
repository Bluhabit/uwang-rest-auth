/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.user.userVerification

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserVerificationRepository : PagingAndSortingRepository<UserVerification, String>,
    CrudRepository<UserVerification, String> {

    fun findByUserActivationToken(userActivationToken: String): UserVerification?


}