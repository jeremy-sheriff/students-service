package org.app.users.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig{

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    val jwkSetUri = ""

    private val jwtAuthConverter = JwtAuthConverter()

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http.csrf{
            it.disable()
        }.authorizeHttpRequests {
            // Permit all requests to /api/health without authentication
            it.requestMatchers("/api/students/health").permitAll()
            // All other requests require authentication
            it.anyRequest().authenticated()
        }

        http. oauth2ResourceServer{
            it.jwt{ x ->
                x.decoder(jwtDecoder())
                x.jwtAuthenticationConverter(jwtAuthConverter)
            }
        }

        http.sessionManagement{
            it.sessionCreationPolicy(STATELESS )
        }

        return http.build()
    }
}