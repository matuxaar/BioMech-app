package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.common.currentTimeMillis
import com.biomech.core.database.dao.DeviceDao
import com.biomech.core.database.entity.CachedDevice
import com.biomech.core.network.OfflineQueueManager
import com.biomech.core.network.api.DeviceApi
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType

class AndroidDeviceRepositoryImpl(
    deviceApi: DeviceApi,
    private val deviceDao: DeviceDao,
    offlineQueueManager: OfflineQueueManager? = null,
) : DeviceRepositoryImpl(deviceApi, offlineQueueManager) {

    override suspend fun afterDevicesFetched(devices: List<Device>) {
        deviceDao.clearAll()
        deviceDao.insertAll(devices.map { it.toCached() })
    }

    override suspend fun getCachedDevices(): AppResult<List<Device>>? {
        val cached = deviceDao.getAll()
        if (cached.isNotEmpty()) {
            return AppResult.Success(cached.map { it.toDomain() })
        }
        return null
    }

    override suspend fun afterDeviceCreated(device: Device) {
        deviceDao.insert(device.toCached())
    }

    override suspend fun afterDeviceDeleted(id: String) {
        deviceDao.deleteById(id)
    }
}

private fun Device.toCached() = CachedDevice(
    id = id,
    type = if (type == DeviceType.PROSTHETIC) "prosthetic" else "sensor",
    name = name,
    hwVersion = hwVersion,
    bleServiceUuid = bleServiceUuid,
    bleCommandCharUuid = bleCommandCharUuid,
    bleStatusCharUuid = bleStatusCharUuid,
    bleEmgCharUuid = bleEmgCharUuid,
    lastRecordingAt = lastRecordingAt?.toLongOrNull(),
    lastTrainingAt = lastTrainingAt?.toLongOrNull(),
    cachedAt = currentTimeMillis(),
)

private fun CachedDevice.toDomain() = Device(
    id = id,
    type = if (type == "prosthetic") DeviceType.PROSTHETIC else DeviceType.SENSOR,
    name = name,
    hwVersion = hwVersion,
    bleServiceUuid = bleServiceUuid,
    bleCommandCharUuid = bleCommandCharUuid,
    bleStatusCharUuid = bleStatusCharUuid,
    bleEmgCharUuid = bleEmgCharUuid,
    lastRecordingAt = lastRecordingAt?.toString(),
    lastTrainingAt = lastTrainingAt?.toString(),
)
