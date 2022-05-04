package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "student")
data class StudentModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "id_person", nullable = false)
    val person: PersonModel? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "student_to_attribute",
        joinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_attribute", referencedColumnName = "id")]
    )
    val attributes: Set<AttributeModel>? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "student_to_filter",
        joinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_filter", referencedColumnName = "id")]
    )
    val filters: Set<FilterModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as StudentModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "person = $person , " +
                "created = $created " +
                ")"
    }
}
