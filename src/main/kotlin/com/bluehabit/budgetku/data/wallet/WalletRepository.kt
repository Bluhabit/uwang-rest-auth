/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.wallet

import com.bluehabit.budgetku.data.apiKey.ApiKey
import com.bluehabit.budgetku.data.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface WalletRepository:PagingAndSortingRepository<Wallet,String>, CrudRepository<Wallet, String> {
    fun findAllByUser(user: User, pageable: Pageable): Page<Wallet>
}