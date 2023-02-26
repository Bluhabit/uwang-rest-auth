/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

fun ResourceBundleMessageSource.translate(key:String):String{
    return getMessage(key,null,LocaleContextHolder.getLocale())
}

fun ResourceBundleMessageSource.translate(key:String, params: Array<out Any>):String{
    return getMessage(key,params,LocaleContextHolder.getLocale())
}