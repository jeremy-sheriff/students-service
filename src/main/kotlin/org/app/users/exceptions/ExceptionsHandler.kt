package org.app.users.exceptions

import org.app.users.dto.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.ResponseEntity

@ControllerAdvice
class ExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseBody
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ResponseDto> {
        val errors = ex.bindingResult.allErrors
            .map { error ->
                val fieldName = (error as FieldError).field
                val errorMessage = error.defaultMessage ?: "Invalid field"
                fieldName to errorMessage
            }
            .toMap()

        val response = ResponseDto(
            status = "error",
            message = "Validation failed",
            data = errors
        )

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response)
    }
}
