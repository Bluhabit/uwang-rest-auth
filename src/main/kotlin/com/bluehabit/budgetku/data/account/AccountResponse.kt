package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.fromOffsetDatetime
import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page


data class AccountResponse(
    var accountId: String? = null,
    var accountNumber: Long? = null,
    var accountSourceName: String? = "",
    var accountBalance: Long? = 0,
    var createdAt: String? = "",
    var updatedAt: String? = ""
)

fun Account.toResponse() = AccountResponse(
    accountId = accountId,
    accountNumber = accountNumber,
    accountSourceName = accountSourceName,
    accountBalance = accountBalance,
    createdAt = createdAt.fromOffsetDatetime(),
    updatedAt = updatedAt.fromOffsetDatetime()
)

fun Page<Account>.toResponse() = pagingResponse<AccountResponse> {
    page = number
    currentSize = size
    items = content.map { it.toResponse() }
    totalData = totalElements
    totalPagesCount = totalPages
}