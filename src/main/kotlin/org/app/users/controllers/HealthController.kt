package org.app.users.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress

@RestController
@RequestMapping("/api/students/health")

class HealthController {

    @GetMapping("")
    fun health(): ResponseEntity<Map<String, String>> {
        val response = HashMap<String, String>()

        // Get the host IP address and hostname
        val inetAddress = InetAddress.getLocalHost()
        val ipAddress = inetAddress.hostAddress
        val hostname = inetAddress.hostName


        // Retrieve the Docker image name from an environment variable
        val dockerImageName = System.getenv("DOCKER_IMAGE_NAME") ?: "Unknown Image"


        response["status"] = "success"
        response["hostIpAddress"] = ipAddress
        response["hostName"] = hostname
        response["ci/cd"] = "test CI/CD"
        response["dockerImageName"] = dockerImageName

        return ResponseEntity.status(200).body(response)
    }
}
