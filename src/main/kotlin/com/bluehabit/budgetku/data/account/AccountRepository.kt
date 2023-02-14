package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.data.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface AccountRepository : PagingAndSortingRepository<Account, String> {
    fun findAllByUser(user: User, pageable: Pageable): Page<Account>

    fun findTopByAccountNumber(accountNumber: Long): Account?

    fun findTopByAccountNumberAndUser(accountNumber: Long,user: User): Account?
}