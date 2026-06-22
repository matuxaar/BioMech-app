package com.biomech.feature.dashboard

import com.biomech.core.ble.BleManager
import com.biomech.core.ble.ConnectionState
import com.biomech.domain.usecase.StartEMGSessionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val deviceConnected: Boolean = false,
    val emgData: List<Float> = emptyList(),
    val isRecording: Boolean = false,
)

class DashboardViewModel(
    private val bleManager: BleManager,
    private val startSessionUseCase: StartEMGSessionUseCase,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    init {
        scope.launch {
            bleManager.connectionState.collect { state ->
                _state.value = _state.value.copy(
                    deviceConnected = state == ConnectionState.CONNECTED
                )
            }
        }

        scope.launch {
            bleManager.emgDataStream.collect { sample ->
                val data = _state.value.emgData.toMutableList()
                data.add(sample.channel1)
                if (data.size > 200) data.removeAt(0)
                _state.value = _state.value.copy(emgData = data)
            }
        }
    }

    fun startRecording() {
        scope.launch {
            // TODO: Start EMG session via API
            _state.value = _state.value.copy(isRecording = true)
        }
    }

    fun stopRecording() {
        scope.launch {
            // TODO: End EMG session via API
            _state.value = _state.value.copy(isRecording = false)
        }
    }
}
