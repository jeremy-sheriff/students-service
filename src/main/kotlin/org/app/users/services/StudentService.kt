package org.app.users.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.app.users.controllers.StudentsController
import org.app.users.dto.StudentExistsResponse
import org.app.users.dto.UserDto
import org.app.users.models.Students
import org.app.users.repositories.UserRepository
import org.jasypt.encryption.StringEncryptor
import org.jasypt.util.text.BasicTextEncryptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest



@Service
class UserService(
    private val usersRepository: UserRepository,
    private val securityService: SecurityService,

    @Value("\${jasypt.encryptor.password}")
    private val encryptionPassword: String,
    private val encryptor: StringEncryptor  // Inject Jasypt Encryptor
) {

    private val topic = System.getenv("KEY_CLOAK_CLIENT_ID")

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun saveUser(userDto: UserDto): HttpStatus {
        if (usersRepository.existsByAdmNo(userDto.admNo)) {
            return HttpStatus.BAD_REQUEST
        } else {
            val user = Students(
                id = UUID.randomUUID(), // Generate UUID
                name = userDto.name,
                admNo = userDto.admNo,
            )
            val objectMapper = jacksonObjectMapper()

            // Persist
            val savedUser = usersRepository.save(user)

            val savedUserMessage = savedUser.id?.let {
                UserDto(
                    id = UUID.randomUUID(),
                    name = userDto.name,
                    admNo = userDto.admNo,
                    course = userDto.course,
                )
            }
            val userJson = objectMapper.writeValueAsString(savedUserMessage)
            println(userJson)
            // kafkaTemplate.send(topic, userJson)
            return HttpStatus.OK
        }
    }

    // Method to fetch users by list of UUIDs
    fun getUsersByIds(ids: List<UUID>): MutableList<Students> {
        return usersRepository.findAllById(ids)
    }

    // Method to get users with pagination and return admission numbers normally (without masking)
    fun getUsers(page: Int, size: Int): Page<Students> {
        val pageable = PageRequest.of(page, size)
        return usersRepository.findAll(pageable)  // Admission numbers returned as-is
    }

    // Method to find a user by UUID and return admission numbers normally (without masking)
    fun findById(uuid: UUID): Optional<Students> {
        return usersRepository.findById(uuid)  // Admission number returned as-is
    }

    // Fetch a single user by admission number
//    @Deprecated("This message is deprecated and no longer maintained")
    //@todo Remove this method in future.
    fun getUserByAdmNo(admNo: String): Students {
        return usersRepository.findByAdmNo(admNo)
    }

    // Check if a student exists by admission number with encryption
    fun studentExistsByAdmNo(admNo: String): Boolean {
        return usersRepository.existsByAdmNo(admNo)
    }

    // Check if a student exists by UUID
    fun studentExistsById(id: UUID): Boolean {
        return usersRepository.existsById(id)
    }

    // Fetch a single user by UUID
    fun getUserById(id: UUID): Optional<Students> {
        return usersRepository.findById(id)
    }

    // Delete a user by UUID
    fun deleteStudent(id: UUID) {
        return usersRepository.deleteById(id)
    }
}
