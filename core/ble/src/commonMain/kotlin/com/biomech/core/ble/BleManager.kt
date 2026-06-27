package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface BleManager {
    val scannedDevices: Flow<List<BleDevice>>
    val emgDataStream: Flow<EMGSample>
    val connectionState: Flow<ConnectionState>
    val characteristicValueStream: Flow<BleCharacteristicValue>

    suspend fun startScanning()
    suspend fun stopScanning()
    suspend fun connect(deviceId: String, serviceUuid: String, notifyCharUuids: List<String>, writeCharUuid: String?)
    suspend fun disconnect()
    suspend fun writeCharacteristic(charUuid: String, data: ByteArray)
    suspend fun discoverCharacteristics(deviceId: String): List<BleCharacteristicInfo>
}

data class BleDevice(
    val id: String,
    val name: String,
    val rssi: Int,
)

data class BleCharacteristicInfo(
    val serviceUuid: String,
    val charUuid: String,
    val supportsNotify: Boolean,
    val supportsWrite: Boolean,
)

data class BleCharacteristicValue(
    val charUuid: String,
    val data: ByteArray,
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING
}
