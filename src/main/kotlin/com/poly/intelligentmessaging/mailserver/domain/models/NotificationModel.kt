package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NotificationModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , created = $created )"
    }
}
