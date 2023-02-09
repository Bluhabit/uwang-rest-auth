package com.bluehabit.budgetku.admin.api_key

import com.bluehabit.budgetku.admin.auth.UserRepository
import com.bluehabit.budgetku.common.exception.DataNotFoundException
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.model.BaseResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

/**
 * Api key service
 * */
@Service
class ApiKeyServiceImpl(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository
) {


    fun generateApiKey(
    ): BaseResponse<ApiKeyResponse> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val user = userRepository
            .findByEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")
        val date = Date().time
        val generateValue = ""

        val apiKey = ApiKey(
            id = date,
            value = generateValue,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )

        val savedData = apiKeyRepository
            .save(apiKey)

        return BaseResponse(
            code = HttpStatus.OK.value(),
            data = savedData.toResponse(),
            message = "Success generate new api key"
        )
    }

    fun deleteApiKey(
        apikeyId: Long
    ): BaseResponse<ApiKeyResponse> {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isBlank()) {
            throw UnAuthorizedException("[98] You don't have access!")
        }
        val user = userRepository
            .findByEmail(email) ?: throw UnAuthorizedException("[98] You don't have permission")

        val findApiKeyOrNull = apiKeyRepository
            .findByIdOrNull(apikeyId) ?: throw DataNotFoundException("Cannot find api key with id $apikeyId")


        apiKeyRepository
            .deleteById(apikeyId)

        return BaseResponse(
            code = HttpStatus.OK.value(),
            data = findApiKeyOrNull.toResponse(),
            message = "Success delete api key with id $apikeyId"
        )

    }
}