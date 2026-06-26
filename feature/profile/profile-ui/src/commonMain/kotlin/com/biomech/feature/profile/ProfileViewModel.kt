package com.biomech.feature.profile

import com.biomech.core.common.AppResult
import com.biomech.core.mvi.BaseAction
import com.biomech.core.mvi.BaseEvent
import com.biomech.core.mvi.BaseState
import com.biomech.core.mvi.BaseViewModel
import com.biomech.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

data class ProfileState(
    val email: String = "",
    val nickname: String = "",
    val deviceCount: Int = 0,
    val completedTrainings: Int = 0,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val updateError: String? = null,
) : BaseState

sealed class ProfileAction : BaseAction {
    data object LoadProfile : ProfileAction()
    data class UpdateNickname(val nickname: String) : ProfileAction()
    data object Logout : ProfileAction()
}

sealed class ProfileEvent : BaseEvent {
    data object NavigateToLogin : ProfileEvent()
}

class ProfileViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel<ProfileState, ProfileAction, ProfileEvent>() {

    override val _state = MutableStateFlow(ProfileState())
    override val _event = Channel<ProfileEvent>(Channel.BUFFERED)

    override suspend fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> loadProfile()
            is ProfileAction.UpdateNickname -> updateNickname(action.nickname)
            ProfileAction.Logout -> {
                authRepository.logout()
                _event.send(ProfileEvent.NavigateToLogin)
            }
        }
    }

    private suspend fun loadProfile() {
        _state.value = _state.value.copy(isLoading = true)
        when (val result = authRepository.getProfile()) {
            is AppResult.Success -> {
                _state.value = _state.value.copy(
                    email = result.data.email,
                    nickname = result.data.nickname,
                    deviceCount = result.data.deviceCount,
                    isLoading = false,
                )
            }
            is AppResult.Error -> {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun updateNickname(nickname: String) {
        _state.value = _state.value.copy(isUpdating = true, updateError = null)
        when (val result = authRepository.updateProfile(nickname)) {
            is AppResult.Success -> {
                _state.value = _state.value.copy(
                    nickname = result.data.nickname,
                    isUpdating = false,
                )
            }
            is AppResult.Error -> {
                _state.value = _state.value.copy(
                    isUpdating = false,
                    updateError = result.message,
                )
            }
        }
    }
}
