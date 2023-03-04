/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import jakarta.transaction.Transactional
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val validationUtil: ValidationUtil,
    private val i18n: ResourceBundleMessageSource
) : BaseService(
    userCredentialRepository,
    i18n
) {
    @Transactional
    fun createNewAccount() = buildResponse(
        allow = {it.allowTo("")}
    ) {

    }
}