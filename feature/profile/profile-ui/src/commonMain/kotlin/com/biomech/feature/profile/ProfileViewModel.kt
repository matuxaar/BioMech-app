package com.biomech.feature.profile

import com.biomech.core.common.AppResult
import com.biomech.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val email: String = "",
    val isLoading: Boolean = false,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun loadProfile() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            authRepository.refreshToken()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun logout() {
        scope.launch {
            authRepository.logout()
        }
    }

    fun setEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }
}
