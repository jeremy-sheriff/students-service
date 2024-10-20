package org.app.users.dto

import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class UserDto(
    val id: UUID? = null, // Make id nullable

    @field:NotEmpty(message = "The name cannot be empty")
    val name: String,

    @field:NotEmpty(message = "The admNo cannot be empty")
    val admNo: String,

    @field:NotEmpty(message = "The course cannot be empty")
    val course: String,
)
