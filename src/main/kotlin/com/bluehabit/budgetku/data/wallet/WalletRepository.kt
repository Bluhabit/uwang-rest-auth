package com.bluehabit.budgetku.data.wallet

import org.springframework.data.repository.PagingAndSortingRepository

interface WalletRepository:PagingAndSortingRepository<Wallet,String> {
}