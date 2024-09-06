package org.app.users.clients

import feign.RequestInterceptor
import feign.RequestTemplate
import org.app.users.dto.CourseDepartmentDto
import org.app.users.services.KeyCloakTokenService
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Configuration
class FeignClientConfig(
    val keyCloakTokenService: KeyCloakTokenService
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            val accessToken = keyCloakTokenService.getToken()
            template.header("Authorization", "Bearer $accessToken")
        }
    }
}

@FeignClient(
    name = "user-client",
    url =  "\${course.url}",
    value =  "",
    path = "api/v1/courses"
)


interface CourseClient {
    @GetMapping("student/{studentId}")
    fun getStudentCourse(@PathVariable studentId: String): MutableList<CourseDepartmentDto>
}