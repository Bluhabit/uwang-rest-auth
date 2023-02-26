package com.bluehabit.budgetku.data.user

import com.bluehabit.budgetku.data.role.Role
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "tb_user")
data class User(
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
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

    @Column
    var userProfilePicture:String,

    @Enumerated(value = EnumType.STRING)
    @Column
    var userAuthProvider: UserAuthProvider = UserAuthProvider.BASIC,

    @Enumerated(value = EnumType.STRING)
    @Column
    var userStatus: UserStatus = UserStatus.NONACTIVE,

    @Column
    var userAuthTokenProvider:String,

    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REFRESH],
    )
    var userRoles:List<Role> = listOf(),

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)