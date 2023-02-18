package com.bluehabit.budgetku.data.wallet

import com.bluehabit.budgetku.data.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface WalletRepository:PagingAndSortingRepository<Wallet,String> {
    fun findAllByUser(user: User,pageable: Pageable): Page<Wallet>
}