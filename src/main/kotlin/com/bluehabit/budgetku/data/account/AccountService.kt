/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.Constants.ErrorCode
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.DuplicateException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.getTodayDateTimeOffset
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import jakarta.transaction.Transactional
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    override val userCredentialRepository: UserCredentialRepository,
    private val validationUtil: ValidationUtil,
    override val i18n: ResourceBundleMessageSource,
    override val errorCode: Int = ErrorCode.CODE_ACCOUNT
) : BaseService() {

    @Transactional
    suspend fun getListAccount(
        pageable: Pageable
    ):BaseResponse<PagingDataResponse<Account>> = buildResponse {

        val findAccount = accountRepository.findAccountByUserEmail(it,pageable)

        baseResponse {
            code = OK.value()
            data = findAccount.toResponse()
            message = "Sukses"
        }
    }
    @Transactional
    suspend fun createNewAccount(
        request: AccountRequest
    ): BaseResponse<Account> = buildResponse() {
        validationUtil.validate(request)

        val findUser = userCredentialRepository.findByUserEmail(it)
            ?: throw DataNotFoundException(
                translate(""),
                errorDataNotFound
            )

        val isExist = accountRepository.exist(request.accountNumber!!)
        if(isExist){
            throw DuplicateException(
                translate(""),
                errorDataAlreadyExist
            )
        }
        val date = getTodayDateTimeOffset()
        val account = Account(
            accountId = null,
            accountBalance = request.accountBalance!!,
            accountNumber = request.accountNumber!!,
            accountSourceName = request.accountSourceName!!,
            user = findUser,
            createdAt = date,
            updatedAt = date
        )
        val savedData = accountRepository.save(
            account
        )

        baseResponse {
            code = OK.value()
            data = savedData
            message = "Sukses"
        }
    }

    @Transactional
    suspend fun updateAccount(
        accountNumber:String,
        request: AccountRequest
    ): BaseResponse<Account> = buildResponse() {
        validationUtil.validate(request)

        userCredentialRepository.findByUserEmail(it)
            ?: throw DataNotFoundException(
                translate(""),
                errorDataNotFound
            )

        val account = accountRepository.findByIdOrNull(accountNumber)
            ?:
            throw DataNotFoundException(
                translate(""),
                errorDataAlreadyExist
            )

        val date = getTodayDateTimeOffset()

        val savedData = accountRepository.save(
            account.copy(
                accountBalance = request.accountBalance!!,
                accountNumber = request.accountNumber!!,
                accountSourceName = request.accountSourceName!!,
                updatedAt = date
            )
        )

        baseResponse {
            code = OK.value()
            data = savedData
            message = "Sukses"
        }
    }

    @Transactional
    suspend fun deleteAccount(
        accountId:String
    ):BaseResponse<Account> = buildResponse {
        val findAccount = accountRepository.findByIdOrNull(accountId)
            ?: throw DataNotFoundException(
                translate(""),
                errorDataNotFound
            )
        accountRepository.deleteById(accountId)

        baseResponse {
            code = OK.value()
            data = findAccount
            message = "Sukses"
        }
    }
}