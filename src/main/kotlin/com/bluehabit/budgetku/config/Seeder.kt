package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.admin.apiKey.v1.ApiKey
import com.bluehabit.budgetku.admin.apiKey.v1.ApiKeyRepository
import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.permission.PermissionRepository
import com.bluehabit.budgetku.data.role.Role
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.LevelUser.USER
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
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
    private val permissionRepository: PermissionRepository,
    private val roleRepository: RoleRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val date = OffsetDateTime.now()

        val permissions = listOf(
            Permission(
                id = "f56917a2-279c-4ad3-8db2-8fc1e54e2be3",
                permissionName = "Manage User",
                permissionType = "WRITE",
                permissionGroup = "budgetku.user",
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "da8bb576-6966-4fb1-88d7-11f0258389ab",
                permissionName = "Manage User",
                permissionType = "READ",
                permissionGroup = "budgetku.user",
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "152e22bf-f2b7-4788-8f54-8b951341bd85",
                permissionName = "Manage Role",
                permissionType = "WRITE",
                permissionGroup = "budgetku.role",
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "08f6e968-a60a-4072-9752-b78fcb9ca736",
                permissionName = "Manage Role",
                permissionType = "READ",
                permissionGroup = "budgetku.role",
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "85f177bb-668f-4bc8-ab67-a181eae0fd4f",
                permissionName = "Manage Category",
                permissionType = "WRITE",
                permissionGroup = "budgetku.category",
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "e4a6b866-e5cf-4b34-b4fa-10323865b149",
                permissionName = "Manage Category",
                permissionType = "READ",
                permissionGroup = "budgetku.category",
                createdAt = date,
                updatedAt = date
            )
        )
        permissionRepository.saveAll(
            permissions
        )

        val permission = permissionRepository.findAll()

        val roleId = "26ff6c62-a447-4e7f-941e-e3c866bd69bf"
        val role = Role(
            id = roleId,
            roleName = "SUPER_ADMIN",
            roleDescription = "Khusus buat Super Admin",
            createdAt = date,
            updatedAt = date
        )
        val saved = roleRepository.save(role)
        roleRepository.save(
            saved.copy(
                permissions = permission.toList(),
            )
        )


        val encoder = BCryptPasswordEncoder(16)
        val result: String = encoder.encode("12345678")
        val email = "admin@bluehabit.com"
        val user = User(
            userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bc",
            userEmail = email,
            userPassword = result,
            userFullName = "Admin blue habit",
            userLevel = USER,
            userAuthProvider = BASIC,
            userDateOfBirth = date,
            userCountryCode = "id",
            userPhoneNumber = "4567890",
            createdAt = date,
            updatedAt = date,
        )
        if (userRepository.findByUserEmail(email) == null) {
            val savedUser = userRepository.save(user)
            userRepository.save(
                savedUser.copy(
                    userRoles = listOf(saved)
                )
            )
        }
        if (apiKeyRepository.findTopByValue("asabVsutafcJsbaKTFt") == null) {
            apiKeyRepository.save(
                ApiKey(
                    id = 456789,
                    value = "asabVsutafcJsbaKTFt",
                    createdAt = date,
                    updatedAt = date
                )

            )
        }
    }

}
