package com.tier2.securityservice.model

import com.tier2.fitness.common.entity.User
import java.util.*

data class UserResponse(
    val uuid: UUID,
    val email: String,
    val name: String,
    val weight: Double,
    val height: Double
)

fun User.toResponse(): UserResponse =
    UserResponse(
        uuid = this.id,
        email = this.email,
        name = this.name,
        weight = this.weight,
        height = this.height,
    )
