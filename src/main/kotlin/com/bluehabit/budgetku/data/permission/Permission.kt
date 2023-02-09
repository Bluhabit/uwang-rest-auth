package com.bluehabit.budgetku.data.permission

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Temporal

@Entity
@Table(
    name = "tb_permission"
)
data class Permission(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id:String? = null,
    @Column(nullable = false)
    var permissionName:String? = null,
    @Column(nullable = false)
    var permissionType:String? = null,
    @Column
    var permissionGroup:String?=null,
    @Column
    var createdAt: OffsetDateTime?=null,
    @Column
    var updatedAt: OffsetDateTime?= null
)