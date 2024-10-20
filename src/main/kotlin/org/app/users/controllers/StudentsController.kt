package org.app.users.controllers

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import jakarta.validation.Valid
import org.app.users.dto.StudentExistsResponse
import org.app.users.dto.UserDto
import org.app.users.models.Students
import org.app.users.services.SqsService
import org.app.users.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Page
import java.util.*

@RestController
@RequestMapping("api/students")
@CrossOrigin(origins = ["http://localhost:4200",
    "https://api.muhohodev.com",
    "https://muhohodev.com"])
@PreAuthorize("hasAnyAuthority('library_role')")
class StudentsController(
    private val userService: UserService,
    private val sqsService: SqsService,


) {
    private val logger = LoggerFactory.getLogger(StudentsController::class.java)

    @GetMapping("all")
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsers")
    fun getUsers(@RequestParam(defaultValue = "0") page: Int,
                 @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Students>> {
        val users = userService.getUsers(page, size)
        return ResponseEntity.ok(users)
    }

    // Fallback method should match the signature of `getUsers`
    fun getAllUsers(page: Int, size: Int, t: Throwable): ResponseEntity<Page<Students>> {
        return ResponseEntity.ok(Page.empty())
    }

    @GetMapping("exists/{admNo}")
    fun studentExists(@PathVariable admNo: String): ResponseEntity<StudentExistsResponse> {
        logger.info("Received request to check if student exists for admission number: $admNo")

        val exists = userService.studentExistsByAdmNo(admNo)

        logger.info("Student with admission number $admNo exists: $exists")

        val response = StudentExistsResponse(exists)
        return ResponseEntity.ok(response)
    }


    @GetMapping("exists/id/{id}")
    fun studentExistsById(@PathVariable id: UUID): ResponseEntity<Boolean> {
        val exists = userService.studentExistsById(id)
        return ResponseEntity.ok(exists)
    }

    @PostMapping("save")
    fun saveUser(@RequestBody @Valid userDto: UserDto): ResponseEntity<Map<String, String>> {
        return try {
            val response = userService.saveUser(userDto)
            if (response.name != "OK") {
                ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to "Failed to save student"))
            } else {
                sqsService.sendJsonMessage(userDto)
                ResponseEntity.ok(mapOf("status" to "success", "message" to "Student saved successfully"))
            }
        } catch (ex: MethodArgumentNotValidException) {
            val errorMessage = ex.bindingResult.allErrors.joinToString { it.defaultMessage ?: "Invalid input" }
            ResponseEntity.badRequest().body(mapOf("status" to "error", "message" to errorMessage))
        }
    }


    @GetMapping("{admNo}")
    fun getUserByAdmNo(@PathVariable admNo: String): ResponseEntity<Students> {
        val student = userService.getUserByAdmNo(admNo)
        return ResponseEntity.ok(student)
    }

    @GetMapping("/id/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<Optional<Students>> {
        val student = userService.getUserById(id)
        return ResponseEntity.ok(student)
    }

    @DeleteMapping("delete/student/{id}")
    fun deleteStudent(@PathVariable id: UUID): ResponseEntity<String> {
        return try {
            userService.deleteStudent(id)
            ResponseEntity.ok("Student with ID $id deleted successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete student with ID $id: ${e.message}")
        }
    }
}

