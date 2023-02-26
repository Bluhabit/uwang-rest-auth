/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.model

data class PagingDataResponse<OUTPUT>(
    var page: Int = 0,
    var currentSize: Int = 0,
    var totalPagesCount: Int = 0,
    var totalData: Long = 0,
    var items: List<OUTPUT> = listOf()
)

fun <OUTPUT> pagingResponse(lambda: PagingDataResponse<OUTPUT>.() -> Unit) = PagingDataResponse<OUTPUT>().apply(lambda)
