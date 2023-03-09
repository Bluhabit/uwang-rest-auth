/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification

import jakarta.validation.constraints.NotBlank

data class NotificationBroadcastRequest(
    @field:NotBlank
    var notificationTitle:String?,
    @field:NotBlank
    var notificationDescription:String?,
    @field:NotBlank
    var notificationBody:String?,
    @field:NotBlank
    var categoryId:String?
)

data class NotificationToUserRequest(
    @field:NotBlank
    var userId:String?,
    @field:NotBlank
    var notificationTitle:String?,
    @field:NotBlank
    var notificationDescription:String?,
    @field:NotBlank
    var notificationBody:String?,
    @field:NotBlank
    var categoryId:String?
)