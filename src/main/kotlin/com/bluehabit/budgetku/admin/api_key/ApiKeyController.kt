package com.bluehabit.budgetku.admin.api_key

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiKeyController(
    private val apiKeyService: ApiKeyServiceImpl
) {
    @PostMapping(
        value = ["api/admin/api-key"],
        produces = ["application/json"]
    )
    fun generateToken(
    )=apiKeyService.generateApiKey()

    @DeleteMapping(
        value = ["api/admin/api-key/{api_key_id}"],
        produces = ["application/json"]
    )
    fun deleteCredential(
        @PathVariable("api_key_id") apiKeyId:Long
    )=apiKeyService.deleteApiKey(apiKeyId)
}