package com.biomech.feature.settings

import com.biomech.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val serverUrl: String = "http://10.0.2.2:8080/api/v1",
)

class SettingsViewModel(
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    fun updateServerUrl(url: String) {
        _state.value = _state.value.copy(serverUrl = url)
    }

    fun logout() {
        scope.launch {
            authRepository.logout()
        }
    }
}
