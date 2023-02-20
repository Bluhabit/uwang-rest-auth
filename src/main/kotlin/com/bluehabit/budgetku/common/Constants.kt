package com.bluehabit.budgetku.common

object Constants {
    object Permission{
        const val USER_PERMISSION = "budgetku.user"
        const val ROLE_PERMISSION = "budgetku.role"
        const val CATEGORY_PERMISSION = "budgetku.category"

        const val HYPEN_READ = ".READ"
        const val READ = "READ"
        const val HYPEN_WRITE = ".WRITE"
        const val WRITE = "WRITE"
    }
    object ActivityType{
        const val SEND_RESET_FR_USER = "SEND_RESET_FOR_USER"
    }
    object BCrypt{
        const val STRENGTH = 16
    }
}