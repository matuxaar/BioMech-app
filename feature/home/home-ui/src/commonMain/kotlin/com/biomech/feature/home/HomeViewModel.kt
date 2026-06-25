package com.biomech.feature.home

import com.biomech.core.common.AppResult
import com.biomech.domain.model.Device
import com.biomech.domain.repository.DeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class HomeViewModel(
    private val deviceRepository: DeviceRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    fun loadDevices() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = deviceRepository.getDevices()) {
                is AppResult.Success -> {
                    _state.value = _state.value.copy(
                        devices = result.data,
                        isLoading = false,
                    )
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }
}
