package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosBleManager : BleManager {
    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices = _scannedDevices.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _emgDataStream = MutableStateFlow(EMGSample(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
    override val emgDataStream = _emgDataStream.asStateFlow()

    private val _characteristicValue = MutableStateFlow(BleCharacteristicValue("", ByteArray(0)))
    override val characteristicValueStream = _characteristicValue.asStateFlow()

    override suspend fun startScanning() { }
    override suspend fun stopScanning() { }

    override suspend fun connect(deviceId: String, serviceUuid: String, notifyCharUuids: List<String>, writeCharUuid: String?) {
        _connectionState.value = ConnectionState.CONNECTING
        _connectionState.value = ConnectionState.CONNECTED
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun writeCharacteristic(charUuid: String, data: ByteArray) { }

    override suspend fun discoverCharacteristics(deviceId: String): List<BleCharacteristicInfo> = emptyList()
}
