package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class SmartBleManager(
    private val realManager: BleManager,
    private val simulatedManager: BleManager = SimulatedBleManager(),
) : BleManager {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: Flow<List<BleDevice>> = _scannedDevices

    override val emgDataStream: Flow<EMGSample> =
        merge(realManager.emgDataStream, simulatedManager.emgDataStream)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: Flow<ConnectionState> = merge(
        _connectionState,
        realManager.connectionState,
        simulatedManager.connectionState,
    )

    override val characteristicValueStream: Flow<BleCharacteristicValue> =
        merge(realManager.characteristicValueStream, simulatedManager.characteristicValueStream)

    private var lastSimDevices: List<BleDevice> = emptyList()
    private var lastRealDevices: List<BleDevice> = emptyList()

    override suspend fun startScanning() {
        realManager.startScanning()
        simulatedManager.startScanning()
        collectDevices()
    }

    override suspend fun stopScanning() {
        realManager.stopScanning()
        simulatedManager.stopScanning()
        _scannedDevices.value = emptyList()
        lastSimDevices = emptyList()
        lastRealDevices = emptyList()
    }

    override suspend fun connect(
        deviceId: String,
        serviceUuid: String,
        notifyCharUuids: List<String>,
        writeCharUuid: String?,
    ) {
        if (deviceId.startsWith("sim-")) {
            simulatedManager.connect(deviceId, serviceUuid, notifyCharUuids, writeCharUuid)
        } else {
            realManager.connect(deviceId, serviceUuid, notifyCharUuids, writeCharUuid)
        }
    }

    override suspend fun disconnect() {
        simulatedManager.disconnect()
        realManager.disconnect()
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun writeCharacteristic(charUuid: String, data: ByteArray) {
        simulatedManager.writeCharacteristic(charUuid, data)
        realManager.writeCharacteristic(charUuid, data)
    }

    override suspend fun discoverCharacteristics(deviceId: String): List<BleCharacteristicInfo> {
        return if (deviceId.startsWith("sim-")) {
            simulatedManager.discoverCharacteristics(deviceId)
        } else {
            realManager.discoverCharacteristics(deviceId)
        }
    }

    private fun collectDevices() {
        scope.launch {
            simulatedManager.scannedDevices.collect { devices ->
                lastSimDevices = devices
                _scannedDevices.value = lastRealDevices + lastSimDevices
            }
        }
        scope.launch {
            realManager.scannedDevices.collect { devices ->
                lastRealDevices = devices
                _scannedDevices.value = lastRealDevices + lastSimDevices
            }
        }
    }
}
