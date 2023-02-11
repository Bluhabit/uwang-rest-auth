package com.bluehabit.budgetku.data.wallet

import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.userdetails.User
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
    name = "tb_wallet"
)
data class Wallet(
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
    @Column
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
