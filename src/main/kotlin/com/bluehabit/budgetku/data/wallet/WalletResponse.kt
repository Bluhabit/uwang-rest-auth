/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.wallet

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page


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

fun Page<Wallet>.toResponse() = pagingResponse<WalletResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}