package org.app.users.models

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@Table(name = "students")
@Entity
open class Students(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    open var id: UUID? = null,

    @Column(name = "courseId", nullable = true)
    open var courseId: String,

    @Column(name = "name", nullable = false)
    open var name: String,

    @Column(name = "adm_no", unique = true, nullable = false)
    open var admNo: String
) {
    constructor() : this(UUID.randomUUID(), "", "","")
}
