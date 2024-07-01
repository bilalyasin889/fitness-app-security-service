package com.tier2.securityservice.model

import com.tier2.fitness.common.entity.Role
import com.tier2.fitness.common.entity.User
import java.util.*

data class UserRequest(
    val email: String,
    val password: String,
    val name: String,
    val weight: Double,
    val height: Double
)

fun UserRequest.toModel(): User =
    User(
        id = UUID.randomUUID(),
        email = this.email,
        password = this.password,
        role = Role.USER,
        name = this.name,
        weight = this.weight,
        height = this.height,
    )
