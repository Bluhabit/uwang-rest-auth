package com.bluehabit.budgetku.data.user

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "tb_user_verification")
data class UserVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userActivationId: String? = null,

    @Column
    var userActivationToken: String,

    @Column
    var userActivationExpired: OffsetDateTime,

    @ManyToOne(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REFRESH],
    )
    var user:User?=null,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)