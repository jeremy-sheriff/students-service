package org.app.users.models

import jakarta.persistence.*
import org.app.users.converter.EncryptionConverter
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

    @Column(name = "name", nullable = false)
    open var name: String,

//    @Convert(converter = EncryptionConverter::class) // Apply converter for encryption
    @Column(name = "adm_no", unique = true, nullable = false)
    open var admNo: String
) {
    constructor() : this(UUID.randomUUID(), "", "")
}
