package com.poly.intelligentmessaging.mailserver.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "filter")
data class FilterModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "id_staff", nullable = false)
    val staffCreator: StaffModel? = null,

    @Column(name = "email")
    @NonNull
    val email: String? = null,

    @Column(name = "name")
    @NonNull
    val name: String? = null,

    @Column(name = "expression")
    val expression: String? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @ManyToMany
    @JoinTable(
        name = "staff_to_filter",
        joinColumns = [JoinColumn(name = "id_filter", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")]
    )
    val staff: Set<StaffModel>? = null,

    @ManyToMany
    @JoinTable(
        name = "student_to_filter",
        joinColumns = [JoinColumn(name = "id_filter", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")]
    )
    val student: Set<StudentModel>? = null,

    @OneToMany
    val virtualUser: Set<MailVirtualUserModel>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as FilterModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "staff = $staff , " +
                "email = $email , " +
                "name = $name , " +
                "expression = $expression , " +
                "created = $created " +
                ")"
    }
}
