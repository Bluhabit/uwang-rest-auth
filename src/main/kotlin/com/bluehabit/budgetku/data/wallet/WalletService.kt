/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.wallet

import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service

@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val validationUtil: ValidationUtil
) {
}