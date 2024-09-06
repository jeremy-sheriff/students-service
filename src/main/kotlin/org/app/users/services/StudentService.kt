package org.app.users.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.app.users.dto.UserDto
import org.app.users.models.Students
import org.app.users.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val usersRepository: UserRepository,
) {

    private val topic = System.getenv("KEY_CLOAK_CLIENT_ID")

    fun saveUser(userDto: UserDto):HttpStatus{
        if (usersRepository.existsByAdmNo(userDto.admNo)){
            return HttpStatus.BAD_REQUEST
        }else{
            val user = Students(
                id = userDto.id,
                name = userDto.name,
                admNo = userDto.admNo,
            )
            val objectMapper = jacksonObjectMapper()

            //Persist
            val savedUser = usersRepository.save(user)

            val savedUserMessage = savedUser.id?.let {
                UserDto(
                    id = it,
                    name = userDto.name,
                    admNo = userDto.admNo,
                    course = userDto.course,
                )
            }
            val userJson = objectMapper.writeValueAsString(savedUserMessage)
            println(userJson)
           //kafkaTemplate.send(topic,userJson)
            return HttpStatus.OK
        }
    }


    fun getUsersByIds(ids: List<Long>): MutableList<Students> {
        return usersRepository.findAllById(ids)
    }


    fun getUsers():MutableList<Students>{
        return usersRepository.findAll()
    }

    fun getUserByAdmNo(admNo:String):Students{
        return usersRepository.findByAdmNo(admNo)
    }

    fun studentExistsByAdmNo(admNo: String):Boolean{
        return usersRepository.existsByAdmNo(admNo)
    }

    fun studentExistsById(id: Long):Boolean{
        return usersRepository.existsById(id)
    }

    fun getUserById(id: String): Optional<Students> {
        return usersRepository.findById(id.toLong())
    }

    fun getCourse(id: Long?) {

    }

}