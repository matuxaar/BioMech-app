package com.biomech.core.network.api

import com.biomech.core.network.ApiConfig
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class StreamSample(
    val ch1: Float,
    val ch2: Float,
    val ch3: Float,
    val ch4: Float,
    val ch5: Float,
    val ch6: Float,
    val ch7: Float,
    val ch8: Float,
)

@Serializable
data class StreamPredictRequest(
    val samples: List<StreamSample>,
    @kotlinx.serialization.SerialName("device_id")
    val deviceId: String? = null,
)

@Serializable
data class StreamPredictResponse(
    val type: String,
    val prediction: String? = null,
    val error: String? = null,
)

class PredictStreamClient {
    private val json = Json { ignoreUnknownKeys = true }
    private var session: WebSocketSession? = null
    private var client: HttpClient? = null
    private val _predictions = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val predictions: SharedFlow<String> = _predictions.asSharedFlow()

    suspend fun connect(scope: CoroutineScope): Boolean {
        val token = ApiConfig.token ?: return false
        val wsUrl = ApiConfig.baseUrl
            .replace("http://", "ws://")
            .replace("https://", "wss://")
            .trimEnd('/') + "/predict/ws?token=$token"

        try {
            val httpClient = HttpClient {
                install(WebSockets)
            }
            client = httpClient
            session = httpClient.webSocketSession(wsUrl)
            scope.launch {
                try {
                    for (frame in session!!.incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val response = json.decodeFromString<StreamPredictResponse>(text)
                            when (response.type) {
                                "prediction" -> response.prediction?.let { _predictions.emit(it) }
                                "error" -> response.error?.let { _predictions.emit("[error] $it") }
                            }
                        }
                    }
                } catch (_: Exception) { }
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    suspend fun send(samples: List<StreamSample>) {
        val request = StreamPredictRequest(samples = samples)
        val text = json.encodeToString(request)
        try {
            session?.send(Frame.Text(text))
        } catch (_: Exception) { }
    }

    suspend fun disconnect() {
        try {
            session?.close()
        } catch (_: Exception) { }
        session = null
        try {
            client?.close()
        } catch (_: Exception) { }
        client = null
    }
}
