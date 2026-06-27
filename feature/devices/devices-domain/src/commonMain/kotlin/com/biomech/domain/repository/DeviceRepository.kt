package com.biomech.domain.repository

import com.biomech.core.common.AppResult
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceAction

interface DeviceRepository {
    suspend fun getDevices(): AppResult<List<Device>>
    suspend fun createDevice(type: String, name: String, hwVersion: String,
        bleServiceUuid: String = "", bleCommandCharUuid: String = "",
        bleStatusCharUuid: String = "", bleEmgCharUuid: String = ""): AppResult<Device>
    suspend fun updateDevice(id: String, name: String?, hwVersion: String?, type: String?,
        bleServiceUuid: String? = null, bleCommandCharUuid: String? = null,
        bleStatusCharUuid: String? = null, bleEmgCharUuid: String? = null): AppResult<Device>
    suspend fun deleteDevice(id: String): AppResult<Unit>
    suspend fun getDeviceActions(deviceId: String): AppResult<List<DeviceAction>>
}
