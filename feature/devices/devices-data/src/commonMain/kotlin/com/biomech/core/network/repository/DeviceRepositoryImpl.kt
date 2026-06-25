package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.common.currentTimeMillis
import com.biomech.core.database.dao.DeviceDao
import com.biomech.core.database.entity.CachedDevice
import com.biomech.core.network.api.DeviceApi
import com.biomech.core.network.dto.CreateDeviceRequest
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType
import com.biomech.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val deviceApi: DeviceApi,
    private val deviceDao: DeviceDao,
) : DeviceRepository {

    override suspend fun getDevices(): AppResult<List<Device>> {
        return try {
            val dtos = deviceApi.getDevices()
            val devices = dtos.map { it.toDomain() }
            val cached = devices.map { it.toCached() }
            deviceDao.clearAll()
            deviceDao.insertAll(cached)
            AppResult.Success(devices)
        } catch (e: Exception) {
            val cached = deviceDao.getAll()
            if (cached.isNotEmpty()) {
                AppResult.Success(cached.map { it.toDomain() })
            } else {
                AppResult.Error(e.message ?: "Failed to fetch devices")
            }
        }
    }

    override suspend fun createDevice(type: String, name: String, hwVersion: String): AppResult<Device> {
        return try {
            val dto = deviceApi.createDevice(CreateDeviceRequest(type, name, hwVersion))
            val device = dto.toDomain()
            deviceDao.insert(device.toCached())
            AppResult.Success(device)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to create device")
        }
    }
}

private fun com.biomech.core.network.dto.DeviceDto.toDomain() = Device(
    id = id,
    type = if (type == "prosthetic") DeviceType.PROSTHETIC else DeviceType.SENSOR,
    name = name,
    hwVersion = hw_version,
)

private fun Device.toCached() = CachedDevice(
    id = id,
    type = if (type == DeviceType.PROSTHETIC) "prosthetic" else "sensor",
    name = name,
    hwVersion = hwVersion,
    cachedAt = currentTimeMillis(),
)

private fun CachedDevice.toDomain() = Device(
    id = id,
    type = if (type == "prosthetic") DeviceType.PROSTHETIC else DeviceType.SENSOR,
    name = name,
    hwVersion = hwVersion,
)
