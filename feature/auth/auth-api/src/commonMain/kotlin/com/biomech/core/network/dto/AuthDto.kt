package com.biomech.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSignInRequest(
    val email: String,
    val password: String,
    @SerialName("returnSecureToken") val returnSecureToken: Boolean,
)

@Serializable
data class FirebaseAuthResponse(
    val idToken: String,
    val refreshToken: String,
    val localId: String,
    val email: String,
)

@Serializable
data class FirebaseTokenRefreshRequest(
    @SerialName("grant_type") val grantType: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class FirebaseTokenRefreshResponse(
    @SerialName("id_token") val idToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: String,
    val email: String,
)

@Serializable
data class FirebaseErrorResponse(
    val error: FirebaseErrorDetail,
)

@Serializable
data class FirebaseErrorDetail(
    val code: Int = 0,
    val message: String = "",
)

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    val nickname: String = "",
    val display_name: String = "",
    val photo_url: String = "",
    val device_count: Int = 0,
)

@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val display_name: String? = null,
    val photo_url: String? = null,
)

@Serializable
data class UploadAvatarResponse(
    val photo_url: String,
)
