package com.bluehabit.budgetku.user

import org.hibernate.annotations.GenericGenerator
import java.security.AuthProvider
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userId: String? = null,

    @Column
    var userFullName: String,

    @Column
    var userDateOfBirth: OffsetDateTime,

    @Column(unique = true)
    var userEmail: String,

    @Column(unique = true)
    var userPhoneNumber: String,

    @Column
    var userCountryCode: String,

    @Column
    var userPassword: String,

    @Enumerated(value = EnumType.STRING)
    @Column
    var userLevel: LevelUser,

    @Enumerated(value = EnumType.STRING)
    @Column
    var userAuthProvider: UserAuthProvider,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)