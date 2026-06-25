package com.biomech.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateDeviceRequest(
    val type: String,
    val name: String,
    val hw_version: String,
)

@Serializable
data class UpdateDeviceRequest(
    val name: String? = null,
    val hw_version: String? = null,
    val type: String? = null,
)

@Serializable
data class DeviceDto(
    val id: String,
    val type: String,
    val name: String,
    val hw_version: String,
)
