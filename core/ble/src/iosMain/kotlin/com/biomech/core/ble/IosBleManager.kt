package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosBleManager : BleManager {
    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices = _scannedDevices.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _emgDataStream = MutableStateFlow(
        EMGSample(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    )
    override val emgDataStream = _emgDataStream.asStateFlow()

    private val _prostheticState = MutableStateFlow(ProstheticState())
    override val prostheticState = _prostheticState.asStateFlow()

    override suspend fun startScanning() { }

    override suspend fun stopScanning() { }

    override suspend fun connect(deviceId: String) {
        _connectionState.value = ConnectionState.CONNECTING
        _connectionState.value = ConnectionState.CONNECTED
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun sendProstheticCommand(command: ProstheticCommand) { }
}
