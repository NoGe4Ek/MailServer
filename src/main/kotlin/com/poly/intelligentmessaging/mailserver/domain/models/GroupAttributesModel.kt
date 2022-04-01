package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "group_attributes")
data class GroupAttributesModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "name")
    val name: String? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_group_attribute")
    val attributes: Set<AttributeModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GroupAttributesModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , created = $created )"
    }
}
