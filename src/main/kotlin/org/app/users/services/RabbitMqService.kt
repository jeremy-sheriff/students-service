package org.app.users.services

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMqService(
    private val rabbitTemplate: RabbitTemplate
) {

    fun dispatchMessage(message: String) {
        rabbitTemplate.convertAndSend("student-queue", message)
        println("Message sent: $message")
    }
}