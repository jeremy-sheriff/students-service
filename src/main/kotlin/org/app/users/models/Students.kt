package org.app.users.models

import jakarta.persistence.*

@Table(name = "students")
@Entity
open class Students(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id:Long?,

    @Column(name = "name", nullable = false)
    open var name: String,

    @Column(name = "admNo", unique = true, nullable = false)
    open var admNo: String
) {
    constructor() : this(1,"","")
}