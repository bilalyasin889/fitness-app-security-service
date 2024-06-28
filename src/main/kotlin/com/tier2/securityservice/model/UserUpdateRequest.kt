package com.tier2.securityservice.model

data class UserUpdateRequest(
    val email: String,
    val name: String,
    val weight: Double,
    val height: Double
)
