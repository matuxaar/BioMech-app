package com.biomech.core.network.api

import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class AuthApi {
    private val client = createHttpClient()

    suspend fun login(email: String, password: String): AuthResponse {
        return client.post("/auth/login") {
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun register(email: String, password: String): AuthResponse {
        return client.post("/auth/register") {
            setBody(RegisterRequest(email, password))
        }.body()
    }

    suspend fun refresh(refreshToken: String): AuthResponse {
        return client.post("/auth/refresh") {
            setBody(mapOf("refresh_token" to refreshToken))
        }.body()
    }
}
