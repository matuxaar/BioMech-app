package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.ApiConfig
import com.biomech.core.network.api.AuthApi
import com.biomech.domain.model.User
import com.biomech.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
) : AuthRepository {

    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun login(email: String, password: String): AppResult<User> {
        return try {
            val response = authApi.login(email, password)
            accessToken = response.access_token
            refreshToken = response.refresh_token
            ApiConfig.token = response.access_token
            AppResult.Success(User(id = response.user.id, email = response.user.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(email: String, password: String): AppResult<User> {
        return try {
            val response = authApi.register(email, password)
            accessToken = response.access_token
            refreshToken = response.refresh_token
            ApiConfig.token = response.access_token
            AppResult.Success(User(id = response.user.id, email = response.user.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun refreshToken(): AppResult<User> {
        return try {
            val response = authApi.refresh(refreshToken ?: return AppResult.Error("No refresh token"))
            accessToken = response.access_token
            refreshToken = response.refresh_token
            ApiConfig.token = response.access_token
            AppResult.Success(User(id = response.user.id, email = response.user.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Token refresh failed")
        }
    }

    override suspend fun logout() {
        accessToken = null
        refreshToken = null
        ApiConfig.token = null
    }

    override suspend fun getToken(): String? = accessToken
}
