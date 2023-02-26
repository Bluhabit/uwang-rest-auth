/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

import com.bluehabit.budgetku.common.exception.BadRequestException
import com.bluehabit.budgetku.data.permission.Permission
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.validation.ConstraintViolationException
import javax.validation.Validator
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType.Object

@Component
class ValidationUtil(
    val validator: Validator,
    val message: ResourceBundleMessageSource
) {
    fun validate(any: Any) {
        val result = validator.validate(any)

        if (result.size != 0) {
            throw ConstraintViolationException(result)
        }
    }

    fun validateDateRange(start: Long, end: Long, maxRange: Int = 3) {
        if (start > end) {
            throw BadRequestException(
                message.translate("date.range.start.overlap")
            )
        }

        val differenceInTime: Long = end - start
        val differenceInDays = (TimeUnit.MILLISECONDS
            .toDays(differenceInTime)
                % 365)

        if (differenceInDays > maxRange) {
            throw BadRequestException(
                message.translate(
                    "date.range.max.overlap",
                    arrayOf(maxRange)
                )
            )
        }

    }

    fun isValid(any: Any): Boolean {
        val result = validator.validate(any)
        return result.size == 0
    }

    fun isValidWithMessage(any: Any): Pair<Boolean, String> {
        return try {
            val result = validator.validate(any)

            Pair(
                result.size == 0,
                result.map { "${it.propertyPath.lastOrNull()?.name} ${it.message}" }.toString()
            )
        } catch (e: Exception) {

            Pair(
                false,
                e.message.toString()
            )
        }
    }

}

fun List<Permission>.isAllowed(to: List<String>): Boolean {
    return this.map { "${it.permissionGroup}.${it.permissionType}" }.containsAll(to)
}