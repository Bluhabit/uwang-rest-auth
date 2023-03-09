/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

object Constants {
    object Permission{

        const val GROUP_NOTIFICATION = "budgetku.notification"
        const val WRITE_NOTIFICATION = "budgetku.notification.WRITE"
        const val READ_NOTIFICATION = "budgetku.notification.READ"

        const val GROUP_USER = "budgetku.user"
        const val WRITE_USER = "budgetku.user.WRITE"
        const val READ_USER = "budgetku.user.READ"


        const val GROUP_ROLE = "budgetku.role"
        const val WRITE_ROLE = "budgetku.role.WRITE"
        const val READ_ROLE = "budgetku.role.READ"


        const val GROUP_CATEGORY = "budgetku.category"
        const val WRITE_CATEGORY = "budgetku.category.WRITE"
        const val READ_CATEGORY = "budgetku.category.READ"

        const val RANDOM = "budgetku.random.READ"

    }

    object ErrorCode{
        const val CODE_BUDGET = 1000
        const val CODE_POST = 2000
        const val CODE_ACCOUNT = 3000
        const val CODE_USER = 4000
        const val CODE_NOTIFICATION = 5000
        const val CODE_ROLE = 6000


    }

    object Notification{
        const val BroadcastTopic = "BudgetKu"
    }
    object BCrypt{
        const val STRENGTH = 2
    }
}