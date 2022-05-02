package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "attribute")
data class AttributeModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinColumn(name = "id_staff", nullable = false)
    val staff: StaffModel? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinColumn(name = "id_dependency", nullable = true)
    val dependency: AttributeModel? = null,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinColumn(name = "id_group_attribute", nullable = false)
    var group: GroupAttributesModel? = null,

    @Column(name = "name")
    @NonNull
    var name: String? = null,

    @Column(name = "expression")
    var expression: String? = null,

    @Column(name = "copy")
    val copy: Boolean? = null,

    @CreationTimestamp
    @Column(name = "created")
    var created: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "student_to_attribute",
        joinColumns = [JoinColumn(name = "id_attribute", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")]
    )
    var students: Set<StudentModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AttributeModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "staff = $staff , " +
                "name = $name , " +
                "expression = $expression , " +
                "created = $created " +
                ")"
    }
}
