package com.biomech.domain.repository

import com.biomech.core.common.AppResult
import com.biomech.domain.model.User
import com.biomech.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(email: String, password: String): AppResult<User>
    suspend fun refreshToken(): AppResult<User>
    suspend fun logout()
    suspend fun getToken(): String?
    suspend fun getProfile(): AppResult<UserProfile>
    suspend fun updateProfile(nickname: String?): AppResult<UserProfile>
}
