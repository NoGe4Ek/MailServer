package com.poly.intelligentmessaging.mailserver.models


import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "staff")
data class StaffModel(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "id_person", nullable = false)
    val person: PersonModel? = null,

    @CreationTimestamp
    @Column(name = "created")
    val created: LocalDateTime? = null,

    @ManyToMany
    @JoinTable(
        name = "mail_auto_forward_map",
        joinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_virtual_user", referencedColumnName = "id")]
    )
    val mailVirtualUserModel: Set<MailVirtualUserModel>? = null,

    @ManyToMany
    @JoinTable(
        name = "role_to_staff",
        joinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_role", referencedColumnName = "id")]
    )
    val roles: Set<RoleModel>? = null,

    @ManyToMany
    @JoinTable(
        name = "staff_to_attribute",
        joinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_attribute", referencedColumnName = "id")]
    )
    val attributes: Set<AttributeModel>? = null,

    @ManyToMany
    @JoinTable(
        name = "staff_to_filter",
        joinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_filter", referencedColumnName = "id")]
    )
    val filters: Set<FilterModel>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as StaffModel

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , person = $person , created = $created )"
    }
}
