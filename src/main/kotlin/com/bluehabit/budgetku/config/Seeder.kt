package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.admin.apiKey.v1.ApiKey
import com.bluehabit.budgetku.admin.apiKey.v1.ApiKeyRepository
import com.bluehabit.budgetku.user.User
import com.bluehabit.budgetku.user.UserRepository
import com.bluehabit.budgetku.common.model.LevelUser.DEV
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

        if (userRepository.findByEmail("admin@bluehabit.com") == null) {
            val encoder = BCryptPasswordEncoder(16)
            val result: String = encoder.encode("12345678")
            val user = User(
                id = date,
                email = "admin@bluehabit.com",
                password = result,
                levelUser = DEV,
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
