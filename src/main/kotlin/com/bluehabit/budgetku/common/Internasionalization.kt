package com.bluehabit.budgetku.common

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object

fun ResourceBundleMessageSource.translate(key:String):String{
    return getMessage(key,null,LocaleContextHolder.getLocale())
}

fun ResourceBundleMessageSource.translate(key:String, params: Array<out Any>):String{
    return getMessage(key,params,LocaleContextHolder.getLocale())
}