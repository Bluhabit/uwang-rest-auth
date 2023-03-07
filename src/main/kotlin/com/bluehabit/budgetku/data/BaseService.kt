/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data

import com.bluehabit.budgetku.common.Constants
import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.data.notification.notification.Notification
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseService() {
    abstract val userCredentialRepository: UserCredentialRepository
    abstract val i18n: ResourceBundleMessageSource
    abstract val errorCode: Int

    companion object {
        const val ERROR_DATA_NOT_FOUND = 10
        const val ERROR_DATA_ALREADY_EXIST = 20
        const val ERROR_NOT_ALLOWED = 40
        const val ERROR_EXPIRED = 50
        const val ERROR_UNKNOWN = 60


    }
    // data not exist(or removed) so process can't be done
     val errorDataNotFound = this.errorCode + ERROR_DATA_NOT_FOUND

    //data exist so new data cannot stored
     val errorDataAlreadyExist = this.errorCode + ERROR_DATA_ALREADY_EXIST

    //user doesn't have permission
    val errorNotAllowed = this.errorCode + ERROR_NOT_ALLOWED
    //token(or session) expired(or invalid)
    val errorExpired= this.errorCode + ERROR_EXPIRED

    //error not defined
    val errorUnknown = this.errorCode + ERROR_UNKNOWN

    fun translate(key: String): String = try {
        i18n.getMessage(key, null, LocaleContextHolder.getLocale())
    } catch (e: Exception) {
        key
    }


    fun translate(key: String, vararg params: String): String = try {
        i18n.getMessage(key, params, LocaleContextHolder.getLocale())
    } catch (e: Exception) {
        key
    }


    fun <Type> buildResponse(
        checkAccess: (Collection<GrantedAuthority>) -> Boolean,
        next: (String) -> Type
    ): Type {

        val context = SecurityContextHolder.getContext().authentication

        val email = context.principal.toString();
        if (email.isEmpty()) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }

        val authority = context.authorities

        if (!checkAccess(authority)) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }

        return next(email)
    }

    fun <Type> buildResponse(
        next: (String) -> Type
    ): Type {
        val context = SecurityContextHolder.getContext().authentication

        if (!context.isAuthenticated) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }

        val email = context.principal.toString();
        if (email.isEmpty()) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }


        return next(email)
    }

    fun sendNotificationToDevice(
        notification: Notification,
        userCredential: UserCredential
    ) {
        try {
            val messages = Message.builder()
                .putAllData(
                    mapOf(
                        "notificationId" to notification.notificationId,
                        "notificationTitle" to notification.notificationTitle,
                        "notificationBody" to notification.notificationBody,
                        "notificationCategory" to notification.notificationCategory?.categoryName.toString(),
                        "notificationDescription" to notification.notificationDescription
                    )
                )
                .setToken(userCredential.userNotificationToken)
                .build()
            FirebaseMessaging.getInstance()
                .send(messages)


        } catch (e: Exception) {
        }
    }

    fun sendNotificationBroadcast(
        notification: Notification
    ) {
        try {
            val messages = Message.builder()
                .putAllData(
                    mapOf(
                        "notificationId" to notification.notificationId,
                        "notificationTitle" to notification.notificationTitle,
                        "notificationBody" to notification.notificationBody,
                        "notificationCategory" to notification.notificationCategory?.categoryName.toString(),
                        "notificationDescription" to notification.notificationDescription
                    )
                )
                .setTopic(Constants.Notification.BroadcastTopic)
                .build()
            FirebaseMessaging.getInstance()
                .send(messages)
        } catch (e: Exception) {
        }
    }
}