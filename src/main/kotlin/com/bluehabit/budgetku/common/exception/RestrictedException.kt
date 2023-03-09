/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common.exception

class RestrictedException(
    override val message: String?,
    val errorCode:Int=0
):Exception()