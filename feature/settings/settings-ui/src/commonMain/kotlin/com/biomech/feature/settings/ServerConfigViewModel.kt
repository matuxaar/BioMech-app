package com.biomech.feature.settings

import com.biomech.core.mvi.BaseAction
import com.biomech.core.mvi.BaseEvent
import com.biomech.core.mvi.BaseState
import com.biomech.core.mvi.BaseViewModel
import com.biomech.core.network.ApiConfig
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout

data class ServerConfigState(
    val serverUrl: String = ApiConfig.baseUrl.trimEnd('/'),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Idle,
) : BaseState

sealed class ServerConfigAction : BaseAction {
    data class UpdateServerUrl(val url: String) : ServerConfigAction()
    data object TestConnection : ServerConfigAction()
    data object GoBack : ServerConfigAction()
}

sealed class ServerConfigEvent : BaseEvent {
    data object NavigateBack : ServerConfigEvent()
    data object ConnectionRestored : ServerConfigEvent()
}

class ServerConfigViewModel : BaseViewModel<ServerConfigState, ServerConfigAction, ServerConfigEvent>() {

    override val _state = MutableStateFlow(ServerConfigState())
    override val _event = Channel<ServerConfigEvent>(Channel.BUFFERED)

    override suspend fun handleAction(action: ServerConfigAction) {
        when (action) {
            is ServerConfigAction.UpdateServerUrl -> {
                _state.value = _state.value.copy(serverUrl = action.url)
            }
            ServerConfigAction.TestConnection -> {
                testConnection()
            }
            ServerConfigAction.GoBack -> {
                _event.send(ServerConfigEvent.NavigateBack)
            }
        }
    }

    private suspend fun testConnection() {
        val url = _state.value.serverUrl.trimEnd('/')
        _state.value = _state.value.copy(connectionStatus = ConnectionStatus.Testing)

        val candidates = listOf(
            "$url/health",
            if (url.contains("/api")) url.substringBefore("/api") + "/health" else null,
            "$url/api/v1/health",
        ).filterNotNull()

        val available = candidates.any { candidate ->
            try {
                val client = HttpClient {
                    install(HttpTimeout) {
                        requestTimeoutMillis = 3_000
                        connectTimeoutMillis = 3_000
                    }
                }
                try {
                    withTimeout(4_000) {
                        client.get(candidate).status == HttpStatusCode.OK
                    }
                } finally {
                    client.close()
                }
            } catch (_: Exception) {
                false
            }
        }

        if (available) {
            ApiConfig.baseUrl = if (url.contains("/api")) url else "$url/api/v1"
            ApiConfig.baseUrl = ApiConfig.baseUrl.trimEnd('/') + "/"
            _state.value = _state.value.copy(connectionStatus = ConnectionStatus.Success)
            _event.send(ServerConfigEvent.ConnectionRestored)
        } else {
            _state.value = _state.value.copy(connectionStatus = ConnectionStatus.Failed)
        }
    }
}
