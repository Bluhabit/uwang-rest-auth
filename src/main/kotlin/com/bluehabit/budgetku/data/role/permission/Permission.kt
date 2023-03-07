/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role.permission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_permission"
)
@SQLDelete(
    sql = "UPDATE tb_permission SET deleted=true WHERE permissionId=?"
)
@Where(
    clause = "deleted = false"
)
data class Permission(
    @Id
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var permissionId:String? = null,
    @Column(nullable = false)
    var permissionName:String? = null,
    @Column(nullable = false)
    var permissionType:String? = null,
    @Column
    var permissionGroup:String?=null,
    @Column
    var createdAt: OffsetDateTime?=null,
    @Column
    var updatedAt: OffsetDateTime?= null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted:Boolean = false
)