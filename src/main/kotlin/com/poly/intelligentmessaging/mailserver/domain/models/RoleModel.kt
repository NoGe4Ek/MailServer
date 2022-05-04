package com.poly.intelligentmessaging.mailserver.domain.models

import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.springframework.lang.NonNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "role")
data class RoleModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "name")
    @NonNull
    val name: String? = null,

    @Column(name = "level")
    @NonNull
    val level: Int? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @ManyToMany
    @JoinTable(
        name = "role_to_staff",
        joinColumns = [JoinColumn(name = "id_role", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")]
    )
    val staff: Set<StaffModel>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RoleModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , created = $created )"
    }
}
