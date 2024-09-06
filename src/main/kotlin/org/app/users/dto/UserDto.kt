package org.app.users.dto

import jakarta.validation.constraints.NotEmpty

data class UserDto(
    val id:Long,

    @field:NotEmpty(message = "The name cannot be empty")
    val name:String,

    @field:NotEmpty(message = "The admNo cannot be empty")
    val admNo:String,

    @field:NotEmpty(message = "The course cannot be empty")
    val course:String,
)