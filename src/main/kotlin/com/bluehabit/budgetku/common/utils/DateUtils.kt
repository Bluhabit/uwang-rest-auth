/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(
    ZoneOffset.UTC
)!!

private val dateTimeFormatterOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME


fun LocalDateTime.format(): String = dateTimeFormatter.format(this)

fun OffsetDateTime.format():String = dateTimeFormatterOffset.format(this)

fun getTodayDateTimeOffset() = OffsetDateTime.now(ZoneOffset.UTC)!!
fun getTodayDateTime() = LocalDateTime.now(ZoneOffset.UTC)!!
fun getExpiredDate() = LocalDateTime.now().toInstant(ZoneOffset.UTC).plus(6, ChronoUnit.HOURS)!!

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