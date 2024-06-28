package com.tier2.securityservice.model

import java.util.*

data class UserResponse(
    val uuid: UUID,
    val email: String,
    val name: String,
    val weight: Double,
    val height: Double
)
