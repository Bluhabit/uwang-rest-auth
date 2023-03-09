/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AccountRequest(
    @field:NotBlank
    var accountName:String?,
    @field:NotBlank
    var accountSourceName:String?,
    @field:NotNull
    var accountBalance:Long?,
    @field:NotNull
    var accountNumber:Long?
)