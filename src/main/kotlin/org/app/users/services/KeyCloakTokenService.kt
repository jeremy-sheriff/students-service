package org.app.users.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.app.users.dto.KeycloakTokenResponse
import org.app.users.security.SecurityConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class KeyCloakTokenService {

    // Add SLF4J logger instance
    private val logger: Logger = LoggerFactory.getLogger(KeyCloakTokenService::class.java)

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/token")
    lateinit var tokenEndpoint: String

    fun getToken(): String {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("grant_type", "password")
            .add("client_id", System.getenv("KEY_CLOAK_CLIENT_ID"))
            .add("username", System.getenv("KEY_CLOAK_USERNAME"))
            .add("password", System.getenv("KEY_CLIENT_PASSWORD"))
            .build()


        val request = Request.Builder()
            .url(tokenEndpoint)
            .post(formBody)
            .build()


        client.newCall(request).execute().use {
            response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseBody = response.body?.string() ?: throw IOException("Response body is null")
            val mapper = jacksonObjectMapper()
            val keycloakTokenResponse: KeycloakTokenResponse = mapper.readValue(responseBody)

            // Handle the response

            // Log the value in securityFilterChain method if needed
            logger.debug("Access token: {}", keycloakTokenResponse.accessToken)
            return keycloakTokenResponse.accessToken
        }

    }
}