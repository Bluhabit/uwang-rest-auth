package com.bluehabit.budgetku.data.wallet

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.data.user.User
import org.springframework.data.domain.Page
import java.time.OffsetDateTime


data class WalletResponse(
    var walletId: String? = null,
    var walletNumber: Long? = null,
    var walletSourceName: String? = "",
    var walletBalance: Long? = 0,
    var createdAt: String? = "",
    var updatedAt: String? = ""
)

fun Wallet.toResponse() = WalletResponse(
    walletId = walletId,
    walletNumber = walletNumber,
    walletSourceName = walletSourceName,
    walletBalance = walletBalance,
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)

fun Page<Wallet>.toListResponse() = content.map { it.toResponse() }