package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "notification")
data class NotificationModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consumer", nullable = false)
    val consumer: StaffModel? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producer", nullable = false)
    val producer: StaffModel? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_filter", nullable = true)
    val filter: FilterModel? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_attribute", nullable = true)
    val attribute: AttributeModel? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null
)
