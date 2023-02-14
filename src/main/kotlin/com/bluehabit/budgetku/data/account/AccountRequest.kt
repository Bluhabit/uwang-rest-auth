package com.bluehabit.budgetku.data.account

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateNewAccountRequest(
    @field:NotNull
    var accountNumber: Long?,
    @field:NotBlank
    var accountSourceName: String?,
    @field:NotNull
    var accountBalance: Long?
)

data class UpdateAccountRequest(
    @field:NotBlank
    var accountSourceName: String?,
    @field:NotNull
    var accountBalance: Long?
)