/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.notification.notification;

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface NotificationRepository : PagingAndSortingRepository<Notification, String>,
    CrudRepository<Notification, String> {


    @Query(
        """SELECT N FROM tb_notification N 
            JOIN N.notificationCategory C
            LEFT JOIN N.notificationRead NR
            WHERE (N.user.userId = ?1 OR N.user.userId IS NULL) 
            AND (NR.userProfile.userId = ?1 OR NR.userProfile.userId IS NULL)
            order by N.createdAt DESC"""
    )
    fun findNotificationByUserId(
        userId: String,
        pageable: Pageable
    ): Page<Notification>

}