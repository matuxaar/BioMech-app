package com.biomech.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
)
