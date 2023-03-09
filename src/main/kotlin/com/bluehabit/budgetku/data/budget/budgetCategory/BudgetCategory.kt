/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.data.budget.budgetCategory

import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where

@Entity
@Table(
    name = "tb_budget_category"
)
@SQLDelete(
    sql = "UPDATE tb_budget_category SET deleted=true WHERE categoryId=?"
)
@Where(
    clause = "deleted = false"
)
data class BudgetCategory(
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
    var updatedAt: OffsetDateTime? = null,
    @Column(
        name = "deleted",
        nullable = false
    )
    var deleted:Boolean = false

)
