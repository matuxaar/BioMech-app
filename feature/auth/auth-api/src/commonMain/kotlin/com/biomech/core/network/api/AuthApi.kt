package com.biomech.core.network.api

import com.biomech.core.network.checkError
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import com.biomech.core.firebase.FirebaseConfig

private val FIREBASE_API_KEY get() = FirebaseConfig.API_KEY

private val firebaseJson = Json { ignoreUnknownKeys = true; isLenient = true }

private fun firebaseClient() = HttpClient {
    install(ContentNegotiation) {
        json(firebaseJson)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
    }
}

private suspend fun HttpResponse.checkFirebaseError(): HttpResponse {
    if (!status.isSuccess()) {
        val body = bodyAsText()
        val errorMsg = try {
            firebaseJson.decodeFromString<FirebaseErrorResponse>(body).error.message
        } catch (_: Exception) {
            body
        }
        throw Exception(errorMsg)
    }
    return this
}

class AuthApi {
    private val client = firebaseClient()
    private val backendClient = createHttpClient()

    suspend fun signInWithPassword(email: String, password: String): FirebaseAuthResponse {
        return client.post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword") {
            parameter("key", FIREBASE_API_KEY)
            setBody(FirebaseSignInRequest(email, password, returnSecureToken = true))
        }.checkFirebaseError().body()
    }

    suspend fun signUp(email: String, password: String): FirebaseAuthResponse {
        return client.post("https://identitytoolkit.googleapis.com/v1/accounts:signUp") {
            parameter("key", FIREBASE_API_KEY)
            setBody(FirebaseSignInRequest(email, password, returnSecureToken = true))
        }.checkFirebaseError().body()
    }

    suspend fun refreshToken(refreshToken: String): FirebaseTokenRefreshResponse {
        return client.post("https://securetoken.googleapis.com/v1/token") {
            parameter("key", FIREBASE_API_KEY)
            setBody(FirebaseTokenRefreshRequest(grantType = "refresh_token", refreshToken = refreshToken))
        }.checkFirebaseError().body()
    }

    suspend fun getProfile(): ProfileDto {
        return backendClient.get("/api/v1/me").checkError().body()
    }

    suspend fun updateProfile(request: UpdateProfileRequest): ProfileDto {
        return backendClient.put("/api/v1/me") {
            setBody(request)
        }.checkError().body()
    }

    suspend fun uploadAvatar(bytes: ByteArray, fileName: String): String {
        val resp = backendClient.post("/api/v1/me/avatar") {
            setBody(MultiPartFormDataContent(formData {
                append("avatar", bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }))
            contentType(ContentType.MultiPart.FormData)
        }.checkError()
        return resp.body<UploadAvatarResponse>().photo_url
    }

    suspend fun getUserById(userId: String): ProfileDto {
        return backendClient.get("/api/v1/users/$userId").checkError().body()
    }

    suspend fun getDashboardStats(): DashboardStatsDto {
        return backendClient.get("/api/v1/stats/dashboard").checkError().body()
    }
}
