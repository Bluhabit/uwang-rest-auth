package com.bluehabit.budgetku.data.category

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(
    name = "tb_category"
)
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var categoryId: String? = null,
    @Column
    var categoryName: String,
    @Column
    var categorySlug: String,
    @Column
    var createdAt: OffsetDateTime? = null,
    @Column
    var updatedAt: OffsetDateTime? = null

)
