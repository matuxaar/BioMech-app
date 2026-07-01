package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.OfflineQueueManager
import com.biomech.core.network.api.DeviceApi
import com.biomech.core.network.dto.*
import com.biomech.core.network.networkJson
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceAction
import com.biomech.domain.model.DeviceType
import com.biomech.domain.repository.DeviceRepository
import kotlinx.serialization.encodeToString

open class DeviceRepositoryImpl(
    protected val deviceApi: DeviceApi,
    protected val offlineQueueManager: OfflineQueueManager? = null,
) : DeviceRepository {

    override suspend fun getDevices(): AppResult<List<Device>> {
        return try {
            val response = deviceApi.getDevices()
            val devices = response.data.map { it.toDomain() }
            afterDevicesFetched(devices)
            AppResult.Success(devices)
        } catch (e: Exception) {
            getCachedDevices() ?: AppResult.Error(e.message ?: "Failed to fetch devices")
        }
    }

    override suspend fun createDevice(
        type: String, name: String, hwVersion: String,
        bleServiceUuid: String, bleCommandCharUuid: String,
        bleStatusCharUuid: String, bleEmgCharUuid: String,
    ): AppResult<Device> {
        return try {
            val dto = deviceApi.createDevice(CreateDeviceRequest(
                type = type,
                name = name,
                hwVersion = hwVersion,
                bleServiceUuid = bleServiceUuid,
                bleCommandCharUuid = bleCommandCharUuid,
                bleStatusCharUuid = bleStatusCharUuid,
                bleEmgCharUuid = bleEmgCharUuid,
            ))
            val device = dto.toDomain()
            afterDeviceCreated(device)
            AppResult.Success(device)
        } catch (e: Exception) {
            val body = networkJson.encodeToString(CreateDeviceRequest(
                type = type, name = name, hwVersion = hwVersion,
                bleServiceUuid = bleServiceUuid, bleCommandCharUuid = bleCommandCharUuid,
                bleStatusCharUuid = bleStatusCharUuid, bleEmgCharUuid = bleEmgCharUuid,
            ))
            offlineQueueManager?.enqueueIfOffline("POST", "/api/v1/devices", body)
            AppResult.Error(e.message ?: "Failed to create device")
        }
    }

    override suspend fun updateDevice(
        id: String, name: String?, hwVersion: String?, type: String?,
        bleServiceUuid: String?, bleCommandCharUuid: String?,
        bleStatusCharUuid: String?, bleEmgCharUuid: String?,
    ): AppResult<Device> {
        return try {
            val dto = deviceApi.updateDevice(id, UpdateDeviceRequest(
                name = name,
                hwVersion = hwVersion,
                type = type,
                bleServiceUuid = bleServiceUuid,
                bleCommandCharUuid = bleCommandCharUuid,
                bleStatusCharUuid = bleStatusCharUuid,
                bleEmgCharUuid = bleEmgCharUuid,
            ))
            AppResult.Success(dto.toDomain())
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("404")) {
                AppResult.Error("Device not found on server. It may have been deleted.")
            } else {
                val body = networkJson.encodeToString(UpdateDeviceRequest(
                    name = name, hwVersion = hwVersion, type = type,
                    bleServiceUuid = bleServiceUuid, bleCommandCharUuid = bleCommandCharUuid,
                    bleStatusCharUuid = bleStatusCharUuid, bleEmgCharUuid = bleEmgCharUuid,
                ))
                offlineQueueManager?.enqueueIfOffline("PUT", "/api/v1/devices/$id", body)
                AppResult.Error(msg)
            }
        }
    }

    override suspend fun deleteDevice(id: String): AppResult<Unit> {
        return try {
            deviceApi.deleteDevice(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("404")) {
                AppResult.Success(Unit)
            } else {
                offlineQueueManager?.enqueueIfOffline("DELETE", "/api/v1/devices/$id")
                AppResult.Error(msg)
            }
        }
    }

    override suspend fun getDeviceActions(deviceId: String): AppResult<List<DeviceAction>> {
        return try {
            val resp = deviceApi.getDeviceActions(deviceId)
            AppResult.Success(resp.actions.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to load device actions")
        }
    }

    protected open suspend fun afterDevicesFetched(devices: List<Device>) {}
    protected open suspend fun getCachedDevices(): AppResult<List<Device>>? = null
    protected open suspend fun afterDeviceCreated(device: Device) {}
}

internal fun DeviceDto.toDomain() = Device(
    id = id,
    type = if (type == "prosthetic") DeviceType.PROSTHETIC else DeviceType.SENSOR,
    name = name,
    hwVersion = hwVersion,
    bleServiceUuid = bleServiceUuid,
    bleCommandCharUuid = bleCommandCharUuid,
    bleStatusCharUuid = bleStatusCharUuid,
    bleEmgCharUuid = bleEmgCharUuid,
    lastRecordingAt = lastRecordingAt,
    lastTrainingAt = lastTrainingAt,
)

internal fun DeviceActionDto.toDomain() = DeviceAction(
    name = name,
    emoji = emoji,
    actionCode = actionCode,
    accuracy = accuracy,
)
