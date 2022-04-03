package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "email")
data class EmailModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "email")
    @NonNull
    val email: String? = null,

    @Column(name = "password")
    @NonNull
    val password: String? = null,

    @Column(name = "mail_directory")
    @NonNull
    val mailDirectory: String? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as EmailModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "email = $email , " +
                "password = $password , " +
                "mailDirectory = $mailDirectory , " +
                "created = $created " +
                ")"
    }
}
