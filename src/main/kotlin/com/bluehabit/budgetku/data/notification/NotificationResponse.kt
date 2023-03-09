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
import org.springframework.data.domain.Page


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