/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.common.model.pagingResponse
import org.springframework.data.domain.Page
import java.time.OffsetDateTime



fun Page<Account>.toResponse() = pagingResponse {
    page = number
    currentSize = size
    items = content.map { it }
    totalData = totalElements
    totalPagesCount = totalPages
}