package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.ValidationUtil
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.data.user.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
    private val validationUtil: ValidationUtil
) {
    suspend fun getListAccountByUser(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<AccountResponse>> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val findUser = userRepository.findByUserEmail(email)
            ?: throw UnAuthorizedException("[98] You don't have access!")

        val findAccountByUser = accountRepository.findAllByUser(
            findUser,
            pageable
        )

        if (findAccountByUser.isEmpty) throw DataNotFoundException(
            "No account corresponding with user account ${findUser.userFullName}"
        )

        return baseResponse {
            code = OK.value()
            data = findAccountByUser.toResponse()
            message = "Success"
        }

    }

    suspend fun createNewAccount(request: CreateNewAccountRequest): BaseResponse<AccountResponse> {
        validationUtil.validate(request)
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val findUser = userRepository.findByUserEmail(email)

        val account = Account(
            accountId = null,
            accountNumber = request.accountNumber!!,
            accountBalance = request.accountBalance!!,
            accountSourceName = request.accountSourceName!!,
            user = findUser,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        val newAccount = accountRepository.save(account)

        return baseResponse {
            code = OK.value()
            data = newAccount.toResponse()
            message = ""
        }
    }

    suspend fun updateAccount(accountNumber: Long, request: UpdateAccountRequest): BaseResponse<AccountResponse> {
        validationUtil.validate(request)
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val findUser = userRepository.findByUserEmail(email)
            ?: throw UnAuthorizedException("[98] You don't have access!")

        val findAccount = accountRepository.findTopByAccountNumberAndUser(
            accountNumber = accountNumber,
            user = findUser
        ) ?: throw DataNotFoundException("Account is not found")

        val updatedData = accountRepository.save(
            findAccount.copy(
                accountSourceName = request.accountSourceName!!,
                accountBalance = request.accountBalance!!
            )
        )

        return baseResponse {
            code = OK.value()
            data = updatedData.toResponse()
            message = " "
        }
    }

    suspend fun deleteAccount(accountNumber: Long): BaseResponse<AccountResponse> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val findUser = userRepository.findByUserEmail(email)
            ?: throw UnAuthorizedException("[98] You don't have access!")

        val findAccount = accountRepository.findTopByAccountNumberAndUser(
            accountNumber = accountNumber,
            user = findUser
        ) ?: throw DataNotFoundException("Account is not found")

        accountRepository.delete(
            findAccount
        )

        return baseResponse {
            code = OK.value()
            data = findAccount.toResponse()
            message = ""
        }
    }
}