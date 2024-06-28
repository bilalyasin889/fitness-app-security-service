package com.tier2.securityservice.controller

import com.tier2.securityservice.entity.toResponse
import com.tier2.securityservice.exception.UserAlreadyExistsException
import com.tier2.securityservice.model.AuthenticationRequest
import com.tier2.securityservice.model.UserRequest
import com.tier2.securityservice.model.toModel
import com.tier2.securityservice.service.AuthenticationService
import com.tier2.securityservice.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): ResponseEntity<String> {
        logger.info("Received login request for username: ${authRequest.email}")
        return try {
            val token = authenticationService.authenticate(authRequest)
            val headers = HttpHeaders()
            headers.set(HttpHeaders.AUTHORIZATION, token)
            return ResponseEntity.ok().headers(headers).build()
        } catch (ex: IllegalArgumentException) {
            logger.error("Failed to authenticate user: ${authRequest.email}. Reason: ${ex.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.message)
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody userRequest: UserRequest
    ): ResponseEntity<*> {
        logger.info("Received registration request for email: ${userRequest.email}")
        return try {
            val response = userService.createUser(userRequest.toModel())
            ResponseEntity.ok(response?.toResponse())
        } catch (ex: UserAlreadyExistsException) {
            logger.error("Failed to register user with email: ${userRequest.email}. Reason: ${ex.message}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
        }
    }
}
