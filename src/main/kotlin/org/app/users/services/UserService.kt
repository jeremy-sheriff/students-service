package org.app.users.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.app.users.dto.CourseDepartmentDto

import org.app.users.dto.UserDto
import org.app.users.models.Students
import org.app.users.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable



@Service
class UserService(
    private val usersRepository: UserRepository,
    private val sqsService: SqsService,
    private val rabbitMqService: RabbitMqService
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun saveUser(userDto: UserDto): HttpStatus {
        // Generate a NEW admission number for each student
        val admNo = getLastAdmNo()
        val user = Students(
            id = UUID.randomUUID(),
            admNo = admNo,
            courseId = userDto.courseId,
            name = userDto.name,
        )
        val objectMapper = jacksonObjectMapper()

        // Persist
        val savedUser = usersRepository.save(user)

        val newUser = UserDto(
            id = savedUser.id,
            name = savedUser.name,
            courseId = userDto.courseId,
        )

        // Send message to Rabbit  MQ
        rabbitMqService.dispatchMessage(newUser.toString())


        val savedUserMessage = savedUser.id?.let {
            UserDto(
                id = UUID.randomUUID(),
                name = userDto.name,
                courseId = userDto.courseId,
            )
        }
//        val userJson = objectMapper.writeValueAsString(savedUserMessage)
//        println(userJson)
        // kafkaTemplate.send(topic, userJson)
        return HttpStatus.OK
    }

    fun getLastAdmNo(): String {
        val lastAdmNo = usersRepository.findLastAdmNo()

        return if (lastAdmNo != null) {
            // Extract the numeric part from "ADM0010"
            val numericPart = lastAdmNo.removePrefix("ADM").toInt()
            // Increment and format with leading zeros
            "ADM${String.format("%04d", numericPart + 1)}"
        } else {
            // If no admission number exists, start with ADM0001
            "ADM0001"
        }
    }

    // Method to fetch users by list of UUIDs
    fun getUsersByIds(ids: List<UUID>): MutableList<Students> {
        return usersRepository.findAllById(ids)
    }

    fun getUsers(page: Int, size: Int): Page<Students> {
        val pageable: Pageable = PageRequest.of(page, size)
        return usersRepository.findAll(pageable)
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
