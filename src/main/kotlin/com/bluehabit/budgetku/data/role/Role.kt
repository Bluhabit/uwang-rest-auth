package com.bluehabit.budgetku.data.role

import com.bluehabit.budgetku.data.permission.Permission
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "tb_role")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: String? = null,
    @Column(nullable = false)
    var roleName: String? = null,
    @Column(nullable = false)
    var roleDescription: String? = null,
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REFRESH]
    )
    var permissions: List<Permission> = listOf(),
    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null
)