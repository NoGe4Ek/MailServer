package com.poly.intelligentmessaging.mailserver.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
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

    @ManyToOne
    @JoinColumn(name = "id_person", nullable = false)
    val person: PersonModel? = null,

    @Column(name = "country")
    @NonNull
    val country: String? = null,

    @Column(name = "department")
    @NonNull
    val department: String? = null,

    @Column(name = "financing")
    @NonNull
    val financing: String? = null,

    @Column(name = "education_format")
    @NonNull
    val educationFormat: String? = null,

    @Column(name = "program_type")
    @NonNull
    val programType: String? = null,

    @Column(name = "direction")
    @NonNull
    val direction: String? = null,

    @Column(name = "directionality")
    @NonNull
    val directionality: String? = null,

    @Column(name = "target_direction")
    @NonNull
    val targetDirection: String? = null,

    @Column(name = "group_number")
    @NonNull
    val groupNumber: String? = null,

    @Column(name = "course")
    @NonNull
    val course: Int? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @ManyToMany
    @JoinTable(
        name = "student_to_attribute",
        joinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_attribute", referencedColumnName = "id")]
    )
    val attributes: Set<AttributeModel>? = null,

    @ManyToMany
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
                "country = $country , " +
                "department = $department , " +
                "financing = $financing , " +
                "educationFormat = $educationFormat , " +
                "programType = $programType , " +
                "direction = $direction , " +
                "directionality = $directionality , " +
                "targetDirection = $targetDirection , " +
                "groupNumber = $groupNumber , " +
                "course = $course , " +
                "created = $created " +
                ")"
    }
}
