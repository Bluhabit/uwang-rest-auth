/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data

import com.bluehabit.budgetku.common.exception.UnAuthorizedException
import com.bluehabit.budgetku.data.permission.Permission
import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.bluehabit.budgetku.data.user.userCredential.UserCredentialRepository
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseService(
    private val userCredentialRepository: UserCredentialRepository,
    private val i18n: ResourceBundleMessageSource
) {
    fun translate(key: String): String {
        return i18n.getMessage(key, null, LocaleContextHolder.getLocale())
    }

    fun translate(key: String, vararg params: String): String {
        return i18n.getMessage(key, params, LocaleContextHolder.getLocale())
    }


    fun <Type> buildResponse(
        allow: (Collection<Permission>) -> Boolean,
        next: (currentUserCredential: UserCredential) -> Type
    ): Type {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isEmpty()) throw UnAuthorizedException(translate("user.not.allowed"))

        val user = userCredentialRepository.findByUserEmail(email)
                ?: throw UnAuthorizedException(translate("user.not.allowed"))

        if (!allow(user.userPermissions)) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }
        return next(user)
    }

    fun <Type> buildResponse(
        next: (userCredential: UserCredential) -> Type
    ): Type {
        val email = SecurityContextHolder.getContext().authentication.principal.toString();
        if (email.isEmpty()) throw UnAuthorizedException(translate("user.not.allowed"))

        val user =
            userCredentialRepository.findByUserEmail(email) ?: throw UnAuthorizedException(translate("user.not.allowed"))

        return next(user)
    }
}