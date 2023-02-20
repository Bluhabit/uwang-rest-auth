package com.bluehabit.budgetku.feature.wallet.v1

import com.bluehabit.budgetku.data.wallet.WalletService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1"]
)
class WalletController(
    private val walletService: WalletService
) {
}