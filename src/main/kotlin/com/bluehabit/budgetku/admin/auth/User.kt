package com.bluehabit.budgetku.admin.auth

import com.bluehabit.budgetku.model.LevelUser
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_user_api_medical_record")
data class User(
    @Id
    @GeneratedValue
    var id: Long?=null,

    @Column(unique = true)
    var email: String,


    @Enumerated(value = EnumType.STRING)
    @Column
    var levelUser: LevelUser,

    @Column
    var password: String,

    @Column
    var createdAt: OffsetDateTime,

    @Column
    var updatedAt: OffsetDateTime
)