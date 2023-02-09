package com.bluehabit.budgetku.config.api_key

import com.bluehabit.budgetku.admin.api_key.ApiKeyRepository
import com.bluehabit.budgetku.admin.auth.User
import com.bluehabit.budgetku.admin.auth.UserRepository
import com.bluehabit.budgetku.model.LevelUser
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
                levelUser = LevelUser.DEV,
                createdAt = offset,
                updatedAt = offset,
            )
            userRepository.save(user)
        }

    }
}