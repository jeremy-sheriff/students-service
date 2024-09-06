package org.app.users.controllers

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import jakarta.validation.Valid
import org.app.users.dto.ResponseDto
import org.app.users.dto.UserDto
import org.app.users.models.Students
import org.app.users.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/students")
@CrossOrigin(origins = ["\${cors.allowed.origins}"])
@PreAuthorize("hasAnyAuthority('library_role')")
class StudentsController(
    private val userService: UserService
) {
    @GetMapping("all")
    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsers")
    fun getUsers():MutableList<Students>{
        return userService.getUsers()
    }

    fun getAllUsers(t:Throwable):MutableList<Students>{
        val students = mutableListOf<Students>()
        students.forEach { student->
            userService.getCourse(student.id)
        }

        return students
    }

    @GetMapping("/ids/{ids}")
    fun getUsersWhereInIds(@PathVariable ids: String): MutableList<Students> {
        val idList = ids.split(",").map { it.toLong() }
        return userService.getUsersByIds(idList)
    }

    @GetMapping("exists/{admNo}")
    fun studentExists(
        @PathVariable admNo: String
    ):Boolean{
        return userService.studentExistsByAdmNo(admNo)
    }

    @GetMapping("exists/id/{id}")
    fun studentExistsById( @PathVariable id: String
    ):Boolean{
        return userService.studentExistsById(id.toLong())
    }

    @PostMapping("save")
    fun saveUser(@RequestBody @Valid userDto: UserDto): ResponseEntity<ResponseDto> {
        return try {
           val response =  userService.saveUser(userDto)
            if(response.name!="OK"){
                println(response.name)
                ResponseEntity.badRequest().build()
            }else{
                ResponseEntity.ok(ResponseDto("Student saved"))
            }
        }catch (ex: MethodArgumentNotValidException){
            ResponseEntity.ok(ResponseDto(ex.message))
        }
    }

    @GetMapping("{admNo}")
    fun getUserByAdmNo(
        @PathVariable admNo:String
    ):Students{
        return userService.getUserByAdmNo(admNo)
    }

    @GetMapping("/id/{id}")
    fun getUserById(
        @PathVariable id:String
    ): Optional<Students> {
        return userService.getUserById(id)
    }




}