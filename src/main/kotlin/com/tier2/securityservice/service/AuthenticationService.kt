package com.tier2.securityservice.service

import com.tier2.securityservice.model.AuthenticationRequest
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsServiceImpl,
    private val tokenService: TokenService
) {
    private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    @Transactional(readOnly = true)
    fun authenticate(authenticationRequest: AuthenticationRequest): String {
        try {
            logger.debug("Attempting authentication for user: ${authenticationRequest.email}")
            val userDetails = authenticationRequest.email.let { userDetailsService.loadUserByUsername(it) }

            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.email,
                    authenticationRequest.password
                )
            )
            logger.info("User ${authenticationRequest.email} authenticated successfully.")
            logger.info("Generating token for ${authenticationRequest.email}.")
            return tokenService.generate(userDetails)
        } catch (ex: UsernameNotFoundException) {
            logger.error("Username not found: ${ex.message}")
            throw IllegalArgumentException(ex.message)
        } catch (e: BadCredentialsException) {
            logger.error("Incorrect password for user: ${authenticationRequest.email}")
            throw IllegalArgumentException("Incorrect username / password")
        }
    }
}