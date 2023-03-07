/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.role.roleGroup

import com.bluehabit.budgetku.data.role.permission.Permission
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_role_group"
)
@SQLDelete(
    sql = "UPDATE tb_role_group SET deleted=true WHERE roleId=?"
)
@Where(
    clause = "deleted = false"
)
data class RoleGroup(
    @Id
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var roleId: String? = null,
    @Column
    var roleName: String,
    @Column
    var roleDescription: String,
    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = [ALL]
    )
    var rolePermissions: Collection<Permission> = listOf(),

    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted: Boolean = false
)
