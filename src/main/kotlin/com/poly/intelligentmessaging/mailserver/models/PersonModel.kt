package com.poly.intelligentmessaging.mailserver.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "person")
data class PersonModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

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

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as PersonModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "lastName = $lastName , " +
                "firstName = $firstName , " +
                "patronymic = $patronymic , " +
                "email = $email , " +
                "created = $created " +
                ")"
    }
}
