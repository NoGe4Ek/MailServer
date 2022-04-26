package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "access")
data class AccessModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_staff", nullable = true)
    val staff: StaffModel? = null,

    @Column(name = "last_name")
    @NonNull
    val lastName: String? = null,

    @Column(name = "first_name")
    @NonNull
    val firstName: String? = null,

    @Column(name = "patronymic")
    @NonNull
    val patronymic: String? = null,

    @Column(name = "email")
    @NonNull
    val email: String? = null,

    @Column(name = "department")
    @NonNull
    val department: String? = null,

    @Column(name = "high_school")
    @NonNull
    val highSchool: String? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AccessModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , lastName = $lastName , firstName = $firstName , patronymic = $patronymic , email = $email , department = $department , highSchool = $highSchool , created = $created )"
    }
}
