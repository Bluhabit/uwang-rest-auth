/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun String?.toOffsetDateTime():OffsetDateTime{
    return this?.let {
        return dateTimeFormatter.parse(
            it,
            OffsetDateTime::from
        )
    } ?: OffsetDateTime.now()
}

fun OffsetDateTime?.fromOffsetDatetime():String{
    return this?.format(dateTimeFormatter) ?: OffsetDateTime.now().fromOffsetDatetime()
}