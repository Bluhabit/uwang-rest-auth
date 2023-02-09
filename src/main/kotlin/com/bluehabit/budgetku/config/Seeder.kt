package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.admin.apiKey.v1.ApiKey
import com.bluehabit.budgetku.admin.apiKey.v1.ApiKeyRepository
import com.bluehabit.budgetku.user.LevelUser
import com.bluehabit.budgetku.user.User
import com.bluehabit.budgetku.user.UserRepository
import com.bluehabit.budgetku.user.LevelUser.DEV
import com.bluehabit.budgetku.user.UserAuthProvider.BASIC
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component
class Seeder(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val date = Date().time
        val offset = OffsetDateTime.now()

        if (userRepository.findByUserEmail("admin@bluehabit.com") == null) {
            val encoder = BCryptPasswordEncoder(16)
            val result: String = encoder.encode("12345678")
            val user = User(
                userId = null,
                userEmail = "admin@bluehabit.com",
                userPassword = result,
                userFullName="Admin blue habit",
                userLevel= LevelUser.USER,
                userAuthProvider=BASIC,
                userDateOfBirth=offset,
                userCountryCode="id",
                userPhoneNumber="4567890",
                createdAt = offset,
                updatedAt = offset,
            )
            userRepository.save(user)
        }
        if (apiKeyRepository.findTopByValue("asabVsutafcJsbaKTFt") == null) {
            apiKeyRepository.save(
                ApiKey(
                    id = 456789,
                    value = "asabVsutafcJsbaKTFt",
                    createdAt = offset,
                    updatedAt = offset
                )

            )
        }
    }

}
