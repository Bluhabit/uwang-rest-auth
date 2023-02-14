package com.bluehabit.budgetku.feature.account.v1

import com.bluehabit.budgetku.data.account.CreateNewAccountRequest
import com.bluehabit.budgetku.data.account.UpdateAccountRequest
import com.bluehabit.budgetku.data.account.AccountService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/api/v1"]
)
class AccountController(
    private val accountService: AccountService
) {
    @GetMapping(
        value = ["/accounts"],
        produces = ["application/json"]
    )
    suspend fun getAccountByUserId(
        pageable: Pageable
    )=accountService.getListAccountByUser(
        pageable
    )

    @PostMapping(
        value = ["/account"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    suspend fun createNewAccount(
        @RequestBody request: CreateNewAccountRequest
    ) = accountService.createNewAccount(
        request
    )

    @PutMapping(
        value = ["/account/{accountNumber}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    suspend fun updateAccount(
        @PathVariable(name = "accountNumber") accountNumber:Long,
        @RequestBody request: UpdateAccountRequest
    ) =accountService.updateAccount(
        accountNumber,
        request
    )

    @DeleteMapping(
        value = ["/account/{accountNumber}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    suspend fun deleteAccount(
        @PathVariable(name = "accountNumber") accountNumber:Long,
    ) =accountService.deleteAccount(
        accountNumber
    )
}