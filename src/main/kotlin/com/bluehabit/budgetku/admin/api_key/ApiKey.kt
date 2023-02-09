package com.bluehabit.budgetku.admin.api_key

import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(
    name = "tb_api_key"
)
data class ApiKey(
    @Id
    val id: Long?=null,


    @Column(name = "value")
    val value: String,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime,

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime
)