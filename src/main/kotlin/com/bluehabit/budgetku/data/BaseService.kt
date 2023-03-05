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
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User

abstract class BaseService(
    private val userCredentialRepository: UserCredentialRepository,
    private val i18n: ResourceBundleMessageSource
) {
    fun translate(key: String): String {
        return try {
            i18n.getMessage(key, null, LocaleContextHolder.getLocale())
        } catch (e: Exception) {
            key
        }
    }

    fun translate(key: String, vararg params: String): String {
        return try {
            i18n.getMessage(key, params, LocaleContextHolder.getLocale())
        } catch (e: Exception) {
            key
        }
    }


    fun <Type> buildResponse(
        allow: (Collection<GrantedAuthority>) -> Boolean,
        next: (String) -> Type
    ): Type {

        val context = SecurityContextHolder.getContext().authentication

        val email = context.principal.toString();
        if (email.isEmpty()) {
            throw UnAuthorizedException(translate("user.not.allowed"))
        }

        val authority = context.authorities

        if (!allow(authority)) {
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
}