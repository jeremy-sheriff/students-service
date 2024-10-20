package org.app.users.dto

data class ResponseDto(
    val status: String,
    val message: String,
    val data: Any? = null
)

