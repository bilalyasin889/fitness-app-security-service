package com.tier2.securityservice.controller

import com.tier2.securityservice.entity.toResponse
import com.tier2.securityservice.model.UserUpdateRequest
import com.tier2.securityservice.service.TokenService
import com.tier2.securityservice.service.UserService
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val tokenService: TokenService
) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/info")
    fun getUserInfo(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<*> {
        return try {
            val email = tokenService.extractEmailFromHeader(authHeader)
                ?: throw EntityNotFoundException("No email provided")

            logger.info("Received request for user information for email [{}]", email)
            val response = userService.getUserByEmail(email)
            ResponseEntity.ok(response?.toResponse())
        } catch (ex: EntityNotFoundException) {
            logger.error("Failed to find user information. Reason: ${ex.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        }
    }

    @PostMapping("/update-info")
    fun updateUserInfo(
        @RequestBody updateRequest: UserUpdateRequest
    ): ResponseEntity<*> {
        return try {
            logger.info("Received request to update user information [{}]", updateRequest.email)
            userService.updateUser(updateRequest)
            ResponseEntity.ok().build<Void>()
        } catch (ex: EntityNotFoundException) {
            logger.error("Failed to update user information. Reason: ${ex.message}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        }
    }

}
