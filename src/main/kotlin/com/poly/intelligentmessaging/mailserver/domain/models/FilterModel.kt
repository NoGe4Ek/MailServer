package com.poly.intelligentmessaging.mailserver.domain.models

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinColumn(name = "id_staff", nullable = false)
    val staff: StaffModel? = null,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "id_email_send")
    val emailSend: EmailModel? = null,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "id_email_answer")
    val emailAnswer: EmailModel? = null,

    @Column(name = "name")
    @NonNull
    var name: String? = null,

    @Column(name = "mode")
    @NonNull
    var mode: String? = null,

    @Column(name = "auto_forward")
    @NonNull
    var autoForward: Boolean? = null,

    @Column(name = "expression")
    var expression: String? = null,

    @Column(name = "copy")
    val copy: Boolean? = null,

    @CreationTimestamp
    @Column(name = "created")
    var created: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "student_to_filter",
        joinColumns = [JoinColumn(name = "id_filter", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "id_student", referencedColumnName = "id")]
    )
    var students: Set<StudentModel>? = null,
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
                "name = $name , " +
                "mode = $mode , " +
                "autoForward = $autoForward , " +
                "expression = $expression , " +
                "created = $created " +
                ")"
    }
}
