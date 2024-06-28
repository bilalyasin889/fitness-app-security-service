package com.tier2.securityservice.model

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
