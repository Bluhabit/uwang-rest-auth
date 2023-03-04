/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.data.user.userCredential.UserCredential
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime

@Entity
@Table(
    name = "tb_account"
)
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var walletId: String? = null,
    @Column
    var walletNumber: Long,
    @Column
    var walletSourceName: String,
    @Column
    var walletBalance: Long,
    @JsonIgnore
    @ManyToOne(
        cascade = [CascadeType.REFRESH],
        fetch = LAZY
    )
    var user: UserCredential? = null,
    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null

)
