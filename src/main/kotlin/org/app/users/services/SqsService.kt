package org.app.users.services

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.ObjectMapper

@Service
class SqsService(
    private val objectMapper: ObjectMapper,  // Injecting the ObjectMapper
    @Value("\${aws.accessKeyId}") private val accessKey: String,
    @Value("\${aws.secretAccessKey}") private val secretKey: String,
    @Value("\${aws.region}") private val region: String,
    @Value("\${aws.sqs.queueUrl}") private val queueUrl: String  // Injecting the SQS queue URL
) {

    private val sqsClient: AmazonSQS = AmazonSQSClientBuilder.standard()
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
        .withRegion(region)
        .build()

    fun <T> sendJsonMessage(payload: T) {
        try {
            val messageBody = objectMapper.writeValueAsString(payload)  // Convert object to JSON
            val sendMessageRequest = sqsClient.sendMessage(queueUrl, messageBody)
            println("JSON message sent. Message ID: ${sendMessageRequest.messageId}")
        } catch (ex: Exception) {
            println("Failed to send message: ${ex.message}")
        }
    }
}