//package org.app.users.services
//
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.stereotype.Service
//
//@Service
//class KafkaService(
//    val kafkaTemplate: KafkaTemplate<String, String>
//) {
//
//
//    fun send(topic:String,payload:String){
//        val objectMapper = jacksonObjectMapper()
//        val userJson = objectMapper.writeValueAsString(payload)
//        kafkaTemplate.send(topic, userJson)
//    }
//}