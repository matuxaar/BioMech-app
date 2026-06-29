package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.ApiConfig
import com.biomech.core.network.api.AuthApi
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.DashboardStatsDto
import com.biomech.core.network.dto.ProfileDto
import com.biomech.core.network.dto.UpdateProfileRequest
import com.biomech.core.storage.KeyValueStorage
import com.biomech.domain.model.DashboardStats
import com.biomech.domain.model.User
import com.biomech.domain.model.UserProfile
import com.biomech.domain.repository.AuthRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

private const val KEY_ACCESS_TOKEN = "auth_access_token"
private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
private const val KEY_USER_ID = "auth_user_id"
private const val KEY_USER_EMAIL = "auth_user_email"

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val storage: KeyValueStorage,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return try {
            val response = authApi.signInWithPassword(email, password)
            saveTokens(response.idToken, response.refreshToken, response.localId, response.email)
            syncUser()
            AppResult.Success(User(id = response.localId, email = response.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(email: String, password: String): AppResult<User> {
        return try {
            val response = authApi.signUp(email, password)
            saveTokens(response.idToken, response.refreshToken, response.localId, response.email)
            syncUser()
            AppResult.Success(User(id = response.localId, email = response.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun refreshToken(): AppResult<User> {
        val savedRefresh = storage.getString(KEY_REFRESH_TOKEN)
        if (savedRefresh.isEmpty()) return AppResult.Error("No refresh token")
        return try {
            val response = authApi.refreshToken(savedRefresh)
            saveTokens(response.idToken, response.refreshToken, response.userId, response.email)
            AppResult.Success(User(id = response.userId, email = response.email))
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Token refresh failed")
        }
    }

    override suspend fun logout() {
        storage.remove(KEY_ACCESS_TOKEN)
        storage.remove(KEY_REFRESH_TOKEN)
        storage.remove(KEY_USER_ID)
        storage.remove(KEY_USER_EMAIL)
        ApiConfig.token = null
    }

    override suspend fun getToken(): String? {
        val token = storage.getString(KEY_ACCESS_TOKEN)
        return token.ifEmpty { null }
    }

    override suspend fun getProfile(): AppResult<UserProfile> {
        return try {
            val profile = authApi.getProfile()
            AppResult.Success(profile.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to load profile")
        }
    }

    override suspend fun updateProfile(nickname: String?, displayName: String?): AppResult<UserProfile> {
        return try {
            val profile = authApi.updateProfile(UpdateProfileRequest(nickname = nickname, display_name = displayName))
            AppResult.Success(profile.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to update profile")
        }
    }

    override suspend fun uploadAvatar(bytes: ByteArray, fileName: String): AppResult<String> {
        return try {
            val photoUrl = authApi.uploadAvatar(bytes, fileName)
            val resolvedUrl = resolveUrl(photoUrl)
            AppResult.Success(resolvedUrl)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to upload avatar")
        }
    }

    override suspend fun getUserById(userId: String): AppResult<UserProfile> {
        return try {
            val profile = authApi.getUserById(userId)
            AppResult.Success(profile.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to load user")
        }
    }

    override suspend fun getDashboardStats(): AppResult<DashboardStats> {
        return try {
            val stats = authApi.getDashboardStats()
            AppResult.Success(stats.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to load stats")
        }
    }

    private suspend fun syncUser() {
        try {
            val token = ApiConfig.token ?: return
            createHttpClient().post("/api/v1/auth/firebase") {
                setBody(mapOf("id_token" to token))
            }
        } catch (_: Exception) { }
    }

    private fun saveTokens(access: String, refresh: String, userId: String, email: String) {
        storage.putString(KEY_ACCESS_TOKEN, access)
        storage.putString(KEY_REFRESH_TOKEN, refresh)
        storage.putString(KEY_USER_ID, userId)
        storage.putString(KEY_USER_EMAIL, email)
        ApiConfig.token = access
    }
}

internal fun resolveUrl(relativeUrl: String): String {
    if (relativeUrl.startsWith("http")) return relativeUrl
    val base = ApiConfig.baseUrl.trimEnd('/')
    val host = base.substringBefore("/api").trimEnd('/')
    return host + relativeUrl
}

private fun ProfileDto.toDomain() = UserProfile(
    email = email,
    nickname = nickname,
    displayName = display_name,
    photoUrl = resolveUrl(photo_url),
    deviceCount = device_count,
)

private fun DashboardStatsDto.toDomain() = DashboardStats(
    deviceCount = deviceCount,
    totalTrainings = totalTrainings,
    completedTrainings = completedTrainings,
    averageAccuracy = averageAccuracy,
    topMovements = topMovements,
)
