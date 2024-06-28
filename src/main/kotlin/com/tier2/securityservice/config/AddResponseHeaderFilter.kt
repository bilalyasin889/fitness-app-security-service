package com.tier2.securityservice.config

import com.tier2.securityservice.service.TokenService
import com.tier2.securityservice.service.UserDetailsServiceImpl
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
@Order(1)
class AddResponseHeaderFilter(
    private val tokenService: TokenService,
    private val userDetailsService: UserDetailsServiceImpl
) : Filter {

    private val logger: Logger = LoggerFactory.getLogger(AddResponseHeaderFilter::class.java)

    private val excludedUrls = listOf("/api/auth")

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        if (shouldSkipFilter(httpRequest.requestURI)) {
            chain?.doFilter(request, response)
        } else {
            val authHeader: String? = httpRequest.getHeader("Authorization")

            if (authHeader != null) {
                try {
                    logger.debug("Intercepting the response to add a new token.")
                    val userEmail = tokenService.extractEmailFromHeader(authHeader)
                    if (userEmail != null) {
                        val userDetails = userDetailsService.loadUserByUsername(userEmail)
                        val newToken = tokenService.generate(userDetails)
                        httpResponse.setHeader("Authorization", newToken)
                        logger.debug("Added new Authorization token to the response header.")
                    }
                } catch (ex: UsernameNotFoundException) {
                    logger.error("User not found: ${ex.message}")
                }
            }
            chain?.doFilter(request, response)
        }
    }

    private fun shouldSkipFilter(requestUri: String): Boolean {
        return excludedUrls.any { requestUri.startsWith(it) }
    }
}
