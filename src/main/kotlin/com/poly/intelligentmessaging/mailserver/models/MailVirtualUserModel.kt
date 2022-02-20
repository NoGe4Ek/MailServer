package com.poly.intelligentmessaging.mailserver.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "mail_virtual_user")
data class MailVirtualUserModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "id_filter", nullable = false)
    val filter: FilterModel? = null,

    @ManyToOne
    @JoinColumn(name = "id_staff", nullable = false)
    val staffCreator: StaffModel? = null,

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

    @ManyToOne
    @JoinTable(
        name = "mail_auto_forward_map",
        joinColumns = [JoinColumn(name = "id_virtual_user", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")]
    )
    val staff: StaffModel? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MailVirtualUserModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(" +
                "id = $id , " +
                "filter = $filter , " +
                "email = $email , " +
                "password = $password , " +
                "mailDirectory = $mailDirectory , " +
                "created = $created " +
                ")"
    }
}
