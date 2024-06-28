package com.tier2.securityservice.service

import com.tier2.securityservice.entity.User
import com.tier2.securityservice.exception.UserAlreadyExistsException
import com.tier2.securityservice.model.UserUpdateRequest
import com.tier2.securityservice.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder
) {
    private val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUser(user: User): User? {
        logger.info("Attempting to create user with email: ${user.email}")

        val found = userRepository.findUserByEmail(user.email)
        return if (found == null) {
            val updated = user.copy(password = encoder.encode(user.password))
            userRepository.save(updated)
            logger.info("User with email ${user.email} created successfully")
            user
        } else {
            logger.warn("User with email ${user.email} already exists")
            throw UserAlreadyExistsException("User with email ${user.email} already exists")
        }
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): User? {
        logger.info("Attempting to get user with email: [$email]")

        return userRepository.findUserByEmail(email)
            ?: throw EntityNotFoundException("Username not found with email [$email]")
    }

    @Transactional
    fun updateUser(updateRequest: UserUpdateRequest) {
        logger.info("Attempting to update user with email: ${updateRequest.email}")

        userRepository.findUserByEmail(updateRequest.email)?.run {
            userRepository.save(copy(
                name = updateRequest.name,
                weight = updateRequest.weight,
                height = updateRequest.height
            ))
            logger.info("User with email ${updateRequest.email} updated successfully")
        } ?: throw EntityNotFoundException("User with email ${updateRequest.email} not found")

    }
}