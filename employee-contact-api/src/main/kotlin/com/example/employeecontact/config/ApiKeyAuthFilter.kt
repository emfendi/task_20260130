package com.example.employeecontact.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class ApiKeyAuthFilter(
    @Value("\${api.security.key}") private val apiKey: String
) : OncePerRequestFilter() {

    companion object {
        private const val API_KEY_HEADER = "X-API-Key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestApiKey = request.getHeader(API_KEY_HEADER)

        if (requestApiKey != null && requestApiKey == apiKey) {
            val authentication = UsernamePasswordAuthenticationToken(
                "api-client",
                null,
                listOf(SimpleGrantedAuthority("ROLE_API_CLIENT"))
            )
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}
