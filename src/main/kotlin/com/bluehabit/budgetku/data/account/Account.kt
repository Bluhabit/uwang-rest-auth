package com.bluehabit.budgetku.data.account

import com.bluehabit.budgetku.data.user.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

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
    var accountId: String? = null,
    @Column(
        unique = true
    )
    var accountNumber: Long,
    @Column
    var accountSourceName: String,
    @Column
    var accountBalance: Long,
    @JsonIgnore
    @ManyToOne(
        cascade = [REFRESH],
        fetch = LAZY
    )
    var user: User? = null,
    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null

)
