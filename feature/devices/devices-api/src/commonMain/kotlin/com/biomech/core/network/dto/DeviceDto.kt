package com.biomech.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateDeviceRequest(
    val type: String,
    val name: String,
    @SerialName("hw_version") val hwVersion: String,
    @SerialName("ble_service_uuid") val bleServiceUuid: String = "",
    @SerialName("ble_command_char_uuid") val bleCommandCharUuid: String = "",
    @SerialName("ble_status_char_uuid") val bleStatusCharUuid: String = "",
    @SerialName("ble_emg_char_uuid") val bleEmgCharUuid: String = "",
)

@Serializable
data class UpdateDeviceRequest(
    val name: String? = null,
    @SerialName("hw_version") val hwVersion: String? = null,
    val type: String? = null,
    @SerialName("ble_service_uuid") val bleServiceUuid: String? = null,
    @SerialName("ble_command_char_uuid") val bleCommandCharUuid: String? = null,
    @SerialName("ble_status_char_uuid") val bleStatusCharUuid: String? = null,
    @SerialName("ble_emg_char_uuid") val bleEmgCharUuid: String? = null,
)

@Serializable
data class DeviceDto(
    val id: String,
    val type: String,
    val name: String,
    @SerialName("hw_version") val hwVersion: String,
    @SerialName("ble_service_uuid") val bleServiceUuid: String = "",
    @SerialName("ble_command_char_uuid") val bleCommandCharUuid: String = "",
    @SerialName("ble_status_char_uuid") val bleStatusCharUuid: String = "",
    @SerialName("ble_emg_char_uuid") val bleEmgCharUuid: String = "",
    @SerialName("last_recording_at") val lastRecordingAt: String? = null,
    @SerialName("last_training_at") val lastTrainingAt: String? = null,
)

@Serializable
data class DeviceActionDto(
    val name: String,
    val emoji: String,
    @SerialName("action_code") val actionCode: Int,
    val accuracy: Double,
)

@Serializable
data class DeviceActionsResponseDto(
    @SerialName("device_id") val deviceId: String,
    val actions: List<DeviceActionDto>,
)
