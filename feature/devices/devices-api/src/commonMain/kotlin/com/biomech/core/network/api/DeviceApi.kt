package com.biomech.core.network.api

import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class DeviceApi {
    private val client = createHttpClient()

    suspend fun getDevices(): List<DeviceDto> {
        return client.get("/api/v1/devices").body()
    }

    suspend fun createDevice(request: CreateDeviceRequest): DeviceDto {
        return client.post("/api/v1/devices") {
            setBody(request)
        }.body()
    }

    suspend fun updateDevice(id: String, request: UpdateDeviceRequest): DeviceDto {
        return client.put("/api/v1/devices/$id") {
            setBody(request)
        }.body()
    }

    suspend fun deleteDevice(id: String) {
        client.delete("/api/v1/devices/$id")
    }
}
