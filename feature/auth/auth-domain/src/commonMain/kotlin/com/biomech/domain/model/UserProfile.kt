package com.biomech.domain.model

data class UserProfile(
    val email: String,
    val nickname: String,
    val displayName: String = "",
    val photoUrl: String = "",
    val deviceCount: Int = 0,
)
