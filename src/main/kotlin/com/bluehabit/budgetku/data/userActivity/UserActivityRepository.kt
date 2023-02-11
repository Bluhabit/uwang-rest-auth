package com.bluehabit.budgetku.data.userActivity

import org.springframework.data.repository.PagingAndSortingRepository

interface UserActivityRepository:PagingAndSortingRepository<UserActivity,String> {
}