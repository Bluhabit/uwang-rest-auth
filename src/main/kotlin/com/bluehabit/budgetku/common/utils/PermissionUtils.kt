/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.utils

import com.bluehabit.budgetku.data.permission.Permission

fun Collection<Permission>.allowTo(vararg permission:String): Boolean {
    return this.map { it.permissionType }.containsAll(permission.toList())
}