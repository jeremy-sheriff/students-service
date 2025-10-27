package org.app.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class UsersApplication
fun main(args: Array<String>) {
    runApplication<UsersApplication>(*args)
}
