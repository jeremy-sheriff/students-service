package org.app.users.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    private val logger: Logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    lateinit var jwkSetUri: String

    private val jwtAuthConverter = JwtAuthConverter()

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        logger.debug("Configuring Security Filter Chain with JWK Set URI: {}", jwkSetUri)

        http.csrf {
            it.disable()
        }
            .cors {}  // Enable CORS
            .authorizeHttpRequests {
                // Permit health endpoint and other specific paths without authentication
                it.requestMatchers("/api/students/health").permitAll()
                it.requestMatchers("/api/students/netsuite/inventory-upload").permitAll()

                // Explicitly allow all OPTIONS requests to pass through (for CORS preflight)
                it.requestMatchers("/**").permitAll()
                it.requestMatchers("/**").permitAll()  // Ensure OPTIONS requests are allowed

                // All other requests require authentication
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { jwtConfigurer ->
                    jwtConfigurer.decoder(jwtDecoder())
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter)
                }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:4200")
        config.addAllowedOrigin("https://muhohodev.com")
        config.addAllowedOrigin("https://api.muhohodev.com")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")  // Allow all methods
        config.maxAge = 3600L  // Cache preflight response for 1 hour

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
