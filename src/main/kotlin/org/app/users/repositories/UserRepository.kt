package org.app.users.repositories

import org.app.users.models.Students
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page
import java.util.*

interface UserRepository:JpaRepository<Students, UUID>{
    fun findByAdmNo(admNo: String): Students

    override fun findById(id: UUID): Optional<Students>

    fun existsByAdmNo(admNo: String):Boolean

//    fun findByAdmNo()

    // Pageable method for getting students with pagination
    override fun findAll(pageable: Pageable): Page<Students>

}