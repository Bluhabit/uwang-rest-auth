package com.bluehabit.budgetku.config.apiKeyMiddleware

import com.bluehabit.budgetku.admin.apiKey.v1.ApiKeyRepository
import com.bluehabit.budgetku.admin.auth.v1.User
import com.bluehabit.budgetku.admin.auth.v1.UserRepository
import com.bluehabit.budgetku.common.model.LevelUser.DEV
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.*

@Component
class ApiKeySeeder(
    private val apiKeyRepository: ApiKeyRepository,
    private val userRepository: UserRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
            val date = Date().time
            val offset = OffsetDateTime.now()


        if (userRepository.findByEmail("admin@cexup.com") == null) {
            val encoder = BCryptPasswordEncoder(16)
            val result: String = encoder.encode("12345678")
            val user = User(
                id = date,
                email = "admin@cexup.com",
                password = result,
                levelUser = DEV,
                createdAt = offset,
                updatedAt = offset,
            )
            userRepository.save(user)
        }

    }
}