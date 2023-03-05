/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification

import com.bluehabit.budgetku.common.model.pagingResponse
import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.notification.notificationRead.NotificationRead
import com.bluehabit.budgetku.data.permission.PermissionReponse
import com.bluehabit.budgetku.data.permission.toResponse
import com.bluehabit.budgetku.data.user.UserCredentialResponse
import com.bluehabit.budgetku.data.user.UserProfileResponse
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userProfile.UserProfile
import org.springframework.data.domain.Page
import java.time.OffsetDateTime



@JvmName("notificationPagingResponse")
fun Page<Notification>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it }
    totalData = totalElements
    totalPagesCount = totalPages
}

@JvmName("notificatonReadPagingResponse")
fun Page<NotificationRead>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it }
    totalData = totalElements
    totalPagesCount = totalPages
}