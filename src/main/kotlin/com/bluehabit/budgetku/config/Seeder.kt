package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.data.apiKey.ApiKey
import com.bluehabit.budgetku.data.apiKey.ApiKeyRepository
import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.permission.PermissionRepository
import com.bluehabit.budgetku.data.role.Role
import com.bluehabit.budgetku.data.role.RoleRepository
import com.bluehabit.budgetku.data.user.User
import com.bluehabit.budgetku.data.user.UserRepository
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

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
                id ="f56917a2-279c-4ad3-8db2-8fc1e54e2be3",
                permissionName = "Manage User",
                permissionType = Constants.Permission.WRITE,
                permissionGroup = Constants.Permission.USER_PERMISSION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "da8bb576-6966-4fb1-88d7-11f0258389ab",
                permissionName = "Manage User",
                permissionType = Constants.Permission.READ,
                permissionGroup =  Constants.Permission.USER_PERMISSION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "152e22bf-f2b7-4788-8f54-8b951341bd85",
                permissionName = "Manage Role",
                permissionType = Constants.Permission.WRITE,
                permissionGroup =  Constants.Permission.ROLE_PERMISSION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "08f6e968-a60a-4072-9752-b78fcb9ca736",
                permissionName = "Manage Role",
                permissionType = Constants.Permission.READ,
                permissionGroup =  Constants.Permission.ROLE_PERMISSION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "85f177bb-668f-4bc8-ab67-a181eae0fd4f",
                permissionName = "Manage Category",
                permissionType = Constants.Permission.WRITE,
                permissionGroup =  Constants.Permission.CATEGORY_PERMISSION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                id = "e4a6b866-e5cf-4b34-b4fa-10323865b149",
                permissionName = "Manage Category",
                permissionType = Constants.Permission.READ,
                permissionGroup =  Constants.Permission.CATEGORY_PERMISSION,
                createdAt = date,
                updatedAt = date
            )
        )

        val encoder = BCryptPasswordEncoder(16)
        val result: String = encoder.encode("12345678")
        val email = "admin@bluehabit.com"
        val email2 = "trian@bluehabit.com"

        permissionRepository.saveAll(permissions)

        val permission = permissionRepository.findAll()


        if (roleRepository.findByRoleName("SUPER_ADMIN") == null) {
            val role = Role(
                roleId = "26ff6c62-a447-4e7f-941e-e3c866bd69bl",
                roleName = "SUPER_ADMIN",
                roleDescription = "Khusus buat Super Admin",
                createdAt = date,
                updatedAt = date
            )
            val savedRole = roleRepository.save(role)
            roleRepository.save(
                savedRole.copy(permissions = permission.toList())
            )

            if (userRepository.findByUserEmail(email) == null) {
                val user = User(
                    userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bc",
                    userEmail = email,
                    userPassword = result,
                    userFullName = "Admin blue habit",
                    userAuthProvider = BASIC,
                    userAuthTokenProvider="",
                    userDateOfBirth = date,
                    userCountryCode = "id",
                    userPhoneNumber = "4567890",
                    createdAt = date,
                    updatedAt = date,
                )
                val savedUser = userRepository.save(user)
                userRepository.save(
                    savedUser.copy(
                        userRoles = listOf(savedRole)
                    )
                )
            }
        }
        if (roleRepository.findByRoleName("USER") == null) {
            val role2 = Role(
                roleId = "26ff6c62-a447-4e7f-941e-e3c866bd69bf",
                roleName = "USER",
                roleDescription = "Khusus buat User",
                createdAt = date,
                updatedAt = date
            )
            val savedRole2 = roleRepository.save(
                role2
            )

            if (userRepository.findByUserEmail(email2) == null) {
                val user2 = User(
                    userId = "26ff6c62-a447-4e7f-941e-e3c866bd69bg",
                    userEmail = email2,
                    userPassword = result,
                    userFullName = "Admin blue habit",
                    userAuthTokenProvider="",
                    userAuthProvider = BASIC,
                    userDateOfBirth = date,
                    userCountryCode = "id",
                    userPhoneNumber = "45678945678",
                    createdAt = date,
                    updatedAt = date,
                )
                val savedUser2 = userRepository.save(user2)
                userRepository.save(
                    savedUser2.copy(
                        userRoles = listOf(savedRole2)
                    )
                )
            }
        }

        if (apiKeyRepository.findTopByValue("jkLBU8LMXAiklTSAHDABhsahxt5sgag") == null) {
            apiKeyRepository.save(
                ApiKey(
                    id = "26ff6c62-a447-4e7f-941e-e3c866bd69bn",
                    value = "jkLBU8LMXAiklTSAHDABhsahxt5sgag",
                    createdAt = date,
                    updatedAt = date
                )

            )
        }
    }

}
