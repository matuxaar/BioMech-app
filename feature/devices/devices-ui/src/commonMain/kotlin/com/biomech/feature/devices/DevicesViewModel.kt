package com.biomech.feature.devices

import com.biomech.core.ble.BleDevice
import com.biomech.core.ble.BleManager
import com.biomech.core.common.AppResult
import com.biomech.core.mvi.BaseAction
import com.biomech.core.mvi.BaseEvent
import com.biomech.core.mvi.BaseState
import com.biomech.core.mvi.BaseViewModel
import com.biomech.domain.repository.DeviceRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class DevicesState(
    val scannedDevices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val isCreating: Boolean = false,
    val createError: String? = null,
    val isUpdating: Boolean = false,
    val updateError: String? = null,
) : BaseState

sealed class DevicesAction : BaseAction {
    data object StartScan : DevicesAction()
    data object StopScan : DevicesAction()
    data class Connect(val deviceId: String) : DevicesAction()
    data class CreateDevice(
        val name: String,
        val type: String,
        val hwVersion: String,
    ) : DevicesAction()
    data class UpdateDevice(
        val id: String,
        val name: String?,
        val hwVersion: String?,
        val type: String?,
    ) : DevicesAction()
    data class DeleteDevice(val id: String) : DevicesAction()
}

sealed class DevicesEvent : BaseEvent {
    data object DeviceCreated : DevicesEvent()
    data object DeviceUpdated : DevicesEvent()
    data object DeviceDeleted : DevicesEvent()
}

class DevicesViewModel(
    private val bleManager: BleManager,
    private val deviceRepository: DeviceRepository,
) : BaseViewModel<DevicesState, DevicesAction, DevicesEvent>() {

    override val _state = MutableStateFlow(DevicesState())
    override val _event = Channel<DevicesEvent>(Channel.BUFFERED)

    init {
        scope.launch {
            bleManager.scannedDevices.collect { devices ->
                _state.value = _state.value.copy(scannedDevices = devices)
            }
        }
    }

    override suspend fun handleAction(action: DevicesAction) {
        when (action) {
            DevicesAction.StartScan -> {
                _state.value = _state.value.copy(isScanning = true)
                bleManager.startScanning()
            }
            DevicesAction.StopScan -> {
                bleManager.stopScanning()
                _state.value = _state.value.copy(isScanning = false)
            }
            is DevicesAction.Connect -> {
                bleManager.connect(action.deviceId)
            }
            is DevicesAction.CreateDevice -> createDevice(action.name, action.type, action.hwVersion)
            is DevicesAction.UpdateDevice -> updateDevice(action.id, action.name, action.hwVersion, action.type)
            is DevicesAction.DeleteDevice -> deleteDevice(action.id)
        }
    }

    private suspend fun createDevice(name: String, type: String, hwVersion: String) {
        _state.value = _state.value.copy(isCreating = true, createError = null)
        when (val result = deviceRepository.createDevice(type, name, hwVersion)) {
            is AppResult.Success -> {
                _state.value = _state.value.copy(isCreating = false)
                _event.send(DevicesEvent.DeviceCreated)
            }
            is AppResult.Error -> {
                _state.value = _state.value.copy(isCreating = false, createError = result.message)
            }
        }
    }

    private suspend fun updateDevice(id: String, name: String?, hwVersion: String?, type: String?) {
        _state.value = _state.value.copy(isUpdating = true, updateError = null)
        when (val result = deviceRepository.updateDevice(id, name, hwVersion, type)) {
            is AppResult.Success -> {
                _state.value = _state.value.copy(isUpdating = false)
                _event.send(DevicesEvent.DeviceUpdated)
            }
            is AppResult.Error -> {
                _state.value = _state.value.copy(isUpdating = false, updateError = result.message)
            }
        }
    }

    private suspend fun deleteDevice(id: String) {
        when (val result = deviceRepository.deleteDevice(id)) {
            is AppResult.Success -> _event.send(DevicesEvent.DeviceDeleted)
            is AppResult.Error -> { /* error handled by caller */ }
        }
    }
}
