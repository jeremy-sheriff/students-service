package org.app.users.services

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService {

    // Method to check if user has view_sensitive_data permission
    fun hasSensitivePermission(): Boolean {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return auth.authorities.any {
            it.authority == "view_sensitive_data"
        }
    }
}
