/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification

import com.bluehabit.budgetku.common.Constants.ErrorCode
import com.bluehabit.budgetku.common.Constants.Permission.WRITE_NOTIFICATION
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.common.model.BaseResponse
import com.bluehabit.budgetku.common.model.PagingDataResponse
import com.bluehabit.budgetku.common.model.baseResponse
import com.bluehabit.budgetku.common.utils.ValidationUtil
import com.bluehabit.budgetku.common.utils.allowTo
import com.bluehabit.budgetku.common.utils.getTodayDateTimeOffset
import com.bluehabit.budgetku.data.BaseService
import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.notification.notification.NotificationRepository
import com.bluehabit.budgetku.data.notification.notificationCategory.NotificationCategoryRepository
import com.bluehabit.budgetku.data.notification.notificationRead.NotificationReadRepository
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import jakarta.transaction.Transactional
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationReadRepository: NotificationReadRepository,
    private val notificationCategoryRepository: NotificationCategoryRepository,
    private val validationUtil: ValidationUtil,
    override val userCredentialRepository: UserCredentialRepository,
    override val i18n: ResourceBundleMessageSource,
    override val errorCode: Int = ErrorCode.CODE_NOTIFICATION
) : BaseService() {

    @Transactional
    fun getNotification(
        pageable: Pageable
    ): BaseResponse<PagingDataResponse<Notification>> = buildResponse {
        val findUser = userCredentialRepository.findByUserEmail(it)
            ?: throw UnAuthorizedException("Yooo")

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

    @Transactional
    fun sendBroadcast(
        request: NotificationBroadcastRequest
    ): BaseResponse<Notification> = buildResponse(
        checkAccess = { it.allowTo(WRITE_NOTIFICATION) }
    ) {
        validationUtil.validate(request)

        val category = notificationCategoryRepository.findByIdOrNull(request.categoryId!!)


        val notification = Notification(
            notificationId = null,
            notificationTitle = request.notificationTitle!!,
            notificationDescription = request.notificationDescription!!,
            notificationRead = listOf(),
            notificationBody = request.notificationBody!!,
            notificationCategory = category,
            createdAt = getTodayDateTimeOffset(),
            updatedAt = getTodayDateTimeOffset()
        )

        val savedNotification = notificationRepository.save(notification)

        this.sendNotificationBroadcast(
            savedNotification,
        )

        baseResponse {
            code = HttpStatus.OK.value()
            data = savedNotification
            message = "Sukses"
        }
    }
}