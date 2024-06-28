package com.tier2.securityservice.repository

import com.tier2.securityservice.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByEmail(email: String): User?
}