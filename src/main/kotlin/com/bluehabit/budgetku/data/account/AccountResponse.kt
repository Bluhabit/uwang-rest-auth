/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime


data class AccountResponse(
    var walletId: String? = null,
    var walletNumber: Long? = null,
    var walletSourceName: String? = "",
    var walletBalance: Long? = 0,
    var createdAt: OffsetDateTime?,
    var updatedAt: OffsetDateTime?
)

fun Account.toResponse() = AccountResponse(
    walletId = walletId,
    walletNumber = walletNumber,
    walletSourceName = walletSourceName,
    walletBalance = walletBalance,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Page<Account>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}