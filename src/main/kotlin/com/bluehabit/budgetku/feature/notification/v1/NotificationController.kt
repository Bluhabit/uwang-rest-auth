/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.feature.notification.v1

import com.bluehabit.budgetku.data.notification.NotificationService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    value = ["/v1/notification"]
)
class NotificationController(
    private val notificationService: NotificationService
) {
    companion object {
        const val json = "application/json"
    }

    @GetMapping(
        value = ["/list-notification"],
        produces = [json]
    )

    fun getListNotification(
        pageable: Pageable
    ) = notificationService.getNotification(
        pageable
    )
}