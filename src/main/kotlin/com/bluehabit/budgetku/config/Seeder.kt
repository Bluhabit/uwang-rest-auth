/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.config

import com.bluehabit.budgetku.common.Constants.Permission.GROUP_CATEGORY
import com.bluehabit.budgetku.common.Constants.Permission.GROUP_NOTIFICATION
import com.bluehabit.budgetku.common.Constants.Permission.GROUP_ROLE
import com.bluehabit.budgetku.common.Constants.Permission.GROUP_USER
import com.bluehabit.budgetku.common.Constants.Permission.READ_CATEGORY
import com.bluehabit.budgetku.common.Constants.Permission.READ_NOTIFICATION
import com.bluehabit.budgetku.common.Constants.Permission.READ_ROLE
import com.bluehabit.budgetku.common.Constants.Permission.READ_USER
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_CATEGORY
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_NOTIFICATION
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_ROLE
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_USER
import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.notification.notification.NotificationRepository
import com.bluehabit.budgetku.data.notification.notificationCategory.NotificationCategory
import com.bluehabit.budgetku.data.notification.notificationCategory.NotificationCategoryRepository
import com.bluehabit.budgetku.data.notification.notificationRead.NotificationReadRepository
import com.bluehabit.budgetku.data.role.permission.Permission
import com.bluehabit.budgetku.data.role.permission.PermissionRepository
import com.bluehabit.budgetku.data.user.UserAuthProvider.BASIC
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import com.bluehabit.budgetku.data.user.userProfile.UserProfileRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime

@Component
class Seeder(
    private val userCredentialRepository: UserCredentialRepository,
    private val userProfileRepository: UserProfileRepository,
    private val permissionRepository: PermissionRepository,
    private val notificationCategoryRepository: NotificationCategoryRepository,
    private val notificationReadRepository: NotificationReadRepository,
    private val notificationRepository: NotificationRepository,
    private val scrypt: SCryptPasswordEncoder
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val date = OffsetDateTime.now()
        val dateOfBirth = LocalDate.of(1998, 9, 16)


        val permissions = listOf(
            Permission(
                permissionId = "f56917a2-279c-4ad3-8db2-8fc1e54e2beo",
                permissionName = "Send,Edit,Delete Notification",
                permissionGroup = GROUP_NOTIFICATION,
                permissionType = WRITE_NOTIFICATION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "da8bb576-6966-4fb1-88d7-11f0258389al",
                permissionName = "Read only notification",
                permissionGroup = GROUP_NOTIFICATION,
                permissionType = READ_NOTIFICATION,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "f56917a2-279c-4ad3-8db2-8fc1e54e2be3",
                permissionName = "Manage User",
                permissionGroup = GROUP_USER,
                permissionType = WRITE_USER,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "da8bb576-6966-4fb1-88d7-11f0258389ab",
                permissionName = "Manage User",
                permissionGroup = GROUP_USER,
                permissionType = READ_USER,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "152e22bf-f2b7-4788-8f54-8b951341bd85",
                permissionName = "Manage Role",
                permissionGroup = GROUP_ROLE,
                permissionType = WRITE_ROLE,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "08f6e968-a60a-4072-9752-b78fcb9ca736",
                permissionName = "Manage Role",
                permissionGroup = GROUP_ROLE,
                permissionType = READ_ROLE,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "85f177bb-668f-4bc8-ab67-a181eae0fd4f",
                permissionName = "Manage Category",
                permissionGroup = GROUP_CATEGORY,
                permissionType = WRITE_CATEGORY,
                createdAt = date,
                updatedAt = date
            ),
            Permission(
                permissionId = "e4a6b866-e5cf-4b34-b4fa-10323865b149",
                permissionName = "Manage Category",
                permissionGroup = GROUP_CATEGORY,
                permissionType = READ_CATEGORY,
                createdAt = date,
                updatedAt = date
            )
        )

        val password: String = scrypt.encode("12345678")
        val email = "admin@bluehabit.com"
        val email2 = "trian@bluehabit.com"

        permissionRepository.saveAll(permissions)

        val permission = permissionRepository.findAll()


        if (userCredentialRepository.findByUserEmail(email) == null) {
            val userId1 = "26ff6c62-a447-4e7f-941e-e3c866bd69bc"
            val userCredential = UserCredential(
                userId = userId1,
                userEmail = email,
                userPassword = password,
                userAuthProvider = BASIC.name,
                userAuthTokenProvider = "",
                userNotificationToken = "",
                createdAt = date,
                updatedAt = date,
            )

            val userProfile = UserProfile(
                userId = userId1,
                userFullName = "Admin blue habit",
                userDateOfBirth = dateOfBirth,
                userCountryCode = "id",
                userPhoneNumber = "4567890",
                userProfilePicture = "",
                createdAt = date,
                updatedAt = date,
            )
            val savedUser1 = userCredentialRepository.save(userCredential)
            val savedProfile1 = userProfileRepository.save(userProfile)
            userCredentialRepository.save(
                savedUser1.copy(
                    userPermissions = permission.toList(),
                    userProfile = savedProfile1
                )
            )

        }


        if (userCredentialRepository.findByUserEmail(email2) == null) {
            val userId2 = "26ff6c62-a447-4e7f-941e-e3c866bd69bg"
            val userCredential2 = UserCredential(
                userId = userId2,
                userEmail = email2,
                userPassword = password,
                userAuthTokenProvider = "",
                userAuthProvider = BASIC.name,
                userNotificationToken = "",
                createdAt = date,
                updatedAt = date,
            )
            val userProfile2 = UserProfile(
                userId = userId2,
                userFullName = "Admin blue habit",
                userDateOfBirth = dateOfBirth,
                userCountryCode = "id",
                userPhoneNumber = "45678945678",
                userProfilePicture = "",
                createdAt = date,
                updatedAt = date,
            )
            val savedUser2 = userCredentialRepository.save(userCredential2)
            val savedProfile2 = userProfileRepository.save(userProfile2)
            userCredentialRepository.save(
                savedUser2.copy(
                    userPermissions = permissions.toList(),
                    userProfile = savedProfile2
                )
            )
        }
        val notificationCategory = notificationCategoryRepository.save(
            NotificationCategory(
                categoryId = "26ff6c62-a447-4e7f-941e-e3c866bd69bg",
                categoryDescription = "Ini Promo",
                categoryName = "Promo",
                createdAt = date,
                updatedAt = date,
                deleted = false
            )
        )

    }


}
