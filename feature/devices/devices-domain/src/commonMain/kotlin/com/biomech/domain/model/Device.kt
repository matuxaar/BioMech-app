package com.biomech.domain.model

data class Device(
    val id: String,
    val type: DeviceType,
    val name: String,
    val hwVersion: String,
    val bleServiceUuid: String = "",
    val bleCommandCharUuid: String = "",
    val bleStatusCharUuid: String = "",
    val bleEmgCharUuid: String = "",
)

enum class DeviceType { PROSTHETIC, SENSOR }

data class DeviceAction(
    val name: String,
    val emoji: String,
    val actionCode: Int,
    val accuracy: Double,
)
