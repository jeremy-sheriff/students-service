package org.app.users.dto

import org.app.users.services.CourseDto
import java.util.UUID

/**
 * DTO for student with course information
 */
data class StudentWithCourseDto(
    val id: UUID?,
    val name: String,
    val admNo : String,
    val course: CourseDto?
)