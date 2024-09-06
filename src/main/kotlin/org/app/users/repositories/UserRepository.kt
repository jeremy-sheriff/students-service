package org.app.users.repositories

import org.app.users.models.Students
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<Students,Long>{
    fun findByAdmNo(admNo: String): Students
    fun existsByAdmNo(admNo: String):Boolean
}