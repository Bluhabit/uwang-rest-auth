/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.notification.notification.NotificationRepository
import com.bluehabit.budgetku.data.notification.notificationCategory.NotificationCategoryRepository
import com.bluehabit.budgetku.data.notification.notificationRead.NotificationReadRepository
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationReadRepository: NotificationReadRepository,
    private val notificationCategoryRepository: NotificationCategoryRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val i18n: ResourceBundleMessageSource,
) : BaseService(
    userCredentialRepository,
    i18n
) {

    fun getNotification(
        pageable: Pageable
    ):BaseResponse<PagingDataResponse<Notification>> = buildResponse {
        val findUser = userCredentialRepository.findByUserEmail(it)
            ?:throw UnAuthorizedException("Yooo")

        val findNotification = notificationRepository.findNotificationByUserId(
            findUser.userId!!,
            pageable
        )

        baseResponse {
            code = HttpStatus.OK.value()
            data = findNotification.toResponse()
            message = "Sukses"
        }
    }
}