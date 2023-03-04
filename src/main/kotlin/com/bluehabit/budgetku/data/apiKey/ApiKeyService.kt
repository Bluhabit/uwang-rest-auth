/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.apiKey

import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.OK
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

/**
 * Api key service
 * */
@Service
class ApiKeyService(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository
) {

    fun getAllApiKeys(pageable: Pageable): BaseResponse<PagingDataResponse<ApiKeyResponse>> {

        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isEmpty()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }

        userRepository.findByUserEmail(email)
            ?: throw UnAuthorizedException("[98] You don't have permission")


        val allApiKeys = apiKeyRepository.findAll(pageable)

        return BaseResponse(
            code = OK.value(),
            data = PagingDataResponse(
                page = allApiKeys.number,
                currentSize = allApiKeys.size,
                totalData = allApiKeys.totalElements,
                totalPagesCount = allApiKeys.totalPages,
                items = allApiKeys.content.map {
                    it.toResponse()
                }
            ),
            message = "Data api keys"
        )
    }

    fun generateApiKey(): BaseResponse<ApiKeyResponse> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString()
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        userRepository
            .findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")
        val date = Date().time
        val generateValue = ""

        val apiKey = ApiKey(
            id = null,
            value = generateValue,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        val savedData = apiKeyRepository
            .save(apiKey)

        return BaseResponse(
            code = OK.value(),
            data = savedData.toResponse(),
            message = "Success generate new api key"
        )
    }

    fun deleteApiKey(
        apikeyId: String
    ): BaseResponse<ApiKeyResponse> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val user = userRepository
            .findByUserEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")

        val findApiKeyOrNull = apiKeyRepository
            .findByIdOrNull(apikeyId) ?: throw DataNotFoundException("Cannot find api key with id $apikeyId")


        apiKeyRepository
            .deleteById(apikeyId)

        return BaseResponse(
            code = OK.value(),
            data = findApiKeyOrNull.toResponse(),
            message = "Success delete api key with id $apikeyId"
        )

    }
}