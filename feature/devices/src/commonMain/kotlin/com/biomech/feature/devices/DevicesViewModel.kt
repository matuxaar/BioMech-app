package com.biomech.feature.devices

import com.biomech.core.ble.BleDevice
import com.biomech.core.ble.BleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DevicesUiState(
    val scannedDevices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
)

class DevicesViewModel(
    private val bleManager: BleManager,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(DevicesUiState())
    val state = _state.asStateFlow()

    init {
        scope.launch {
            bleManager.scannedDevices.collect { devices ->
                _state.value = _state.value.copy(scannedDevices = devices)
            }
        }
    }

    fun startScan() {
        scope.launch {
            _state.value = _state.value.copy(isScanning = true)
            bleManager.startScanning()
        }
    }

    fun stopScan() {
        scope.launch {
            bleManager.stopScanning()
            _state.value = _state.value.copy(isScanning = false)
        }
    }

    fun connect(deviceId: String) {
        scope.launch {
            bleManager.connect(deviceId)
        }
    }
}
