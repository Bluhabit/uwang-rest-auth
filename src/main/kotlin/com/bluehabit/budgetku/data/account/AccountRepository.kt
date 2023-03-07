/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface AccountRepository:PagingAndSortingRepository<Account,String>, CrudRepository<Account, String> {
    fun findAllByUser(user: UserCredential, pageable: Pageable): Page<Account>

    @Query("SELECT case when count(m) > 0 then true else false end from Account as m where m.accountNumber =:userEmail")
    fun exist(
        accountNumber: Long,
    ):Boolean


    @Query("select a from Account a where a.user.userId = ?1")
    fun findAccountByUserId(userId: String, pageable: Pageable): Page<Account>


    @Query("select a from Account a where a.user.userEmail = ?1")
    fun findAccountByUserEmail(userEmail: String, pageable: Pageable): Page<Account>

}