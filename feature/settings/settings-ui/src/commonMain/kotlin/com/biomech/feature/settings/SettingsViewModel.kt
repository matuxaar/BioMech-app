package com.biomech.feature.settings

import com.biomech.core.mvi.BaseAction
import com.biomech.core.mvi.BaseEvent
import com.biomech.core.mvi.BaseState
import com.biomech.core.mvi.BaseViewModel
import com.biomech.core.network.ApiConfig
import com.biomech.domain.repository.AuthRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout

data class SettingsState(
    val serverUrl: String = ApiConfig.baseUrl.trimEnd('/'),
) : BaseState

sealed class SettingsAction : BaseAction {
    data class UpdateServerUrl(val url: String) : SettingsAction()
    data object TestConnection : SettingsAction()
    data object Logout : SettingsAction()
}

sealed class SettingsEvent : BaseEvent {
    data object NavigateToLogin : SettingsEvent()
    data object ConnectionRestored : SettingsEvent()
}

class SettingsViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel<SettingsState, SettingsAction, SettingsEvent>() {

    override val _state = MutableStateFlow(SettingsState())
    override val _event = Channel<SettingsEvent>(Channel.BUFFERED)

    override suspend fun handleAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.UpdateServerUrl -> {
                _state.value = _state.value.copy(serverUrl = action.url)
            }
            SettingsAction.TestConnection -> {
                val url = _state.value.serverUrl
                val available = testUrl(url)
                if (available) {
                    ApiConfig.baseUrl = url.trimEnd('/') + "/"
                    _event.send(SettingsEvent.ConnectionRestored)
                }
            }
            SettingsAction.Logout -> {
                authRepository.logout()
                _event.send(SettingsEvent.NavigateToLogin)
            }
        }
    }

    private suspend fun testUrl(url: String): Boolean {
        val healthUrl = buildString {
            val clean = url.trimEnd('/')
            val idx = clean.indexOf("/api")
            append(if (idx > 0) clean.substring(0, idx) + "/health" else "$clean/health")
        }
        return try {
            val client = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 4_000
                    connectTimeoutMillis = 4_000
                }
            }
            withTimeout(5_000) {
                val response = client.get(healthUrl)
                client.close()
                response.status == HttpStatusCode.OK
            }
        } catch (_: Exception) {
            false
        }
    }
}
