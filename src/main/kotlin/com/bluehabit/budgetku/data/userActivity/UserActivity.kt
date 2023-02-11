package com.bluehabit.budgetku.data.userActivity

import com.bluehabit.budgetku.data.user.User
import org.hibernate.annotations.GenericGenerator
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "tb_user_activity"
)
data class UserActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var userActivityId: String? = null,
    @Column
    var activityDescription: String? = null,
    @Column
    var activityType: String? = null,
    @Column
    var activityRef: String? = null,
    @ManyToOne
    @Column
    var user: User? = null,
    @Column
    var createdAt: OffsetDateTime,
    @Column
    var updatedAt: OffsetDateTime

)
