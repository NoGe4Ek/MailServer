package com.poly.intelligentmessaging.mailserver.domain.models


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

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "id_person", nullable = false)
    val person: PersonModel? = null,

    @JoinColumn(name = "password", nullable = false)
    var password: String? = null,

    @CreationTimestamp
    @Column(name = "created")
    var created: LocalDateTime? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], mappedBy = "staff", orphanRemoval = true)
    val filters: Set<FilterModel>? = null,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REMOVE], mappedBy = "staff", orphanRemoval = true)
//    @JoinColumn(name = "id_staff")
    val attributes: Set<AttributeModel>? = null,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], mappedBy = "staff", orphanRemoval = true)
//    @JoinColumn(name = "id_staff")
    val groups: Set<GroupAttributesModel>? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "role_to_staff",
        joinColumns = [JoinColumn(name = "id_staff", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_role", referencedColumnName = "id")]
    )
    var roles: Set<RoleModel>? = null
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
