package com.biomech.feature.dashboard

import com.biomech.core.ble.BleManager
import com.biomech.core.ble.ConnectionState
import com.biomech.core.ble.ProstheticCommand
import com.biomech.core.ble.ProstheticState
import com.biomech.core.mvi.BaseAction
import com.biomech.core.mvi.BaseEvent
import com.biomech.core.mvi.BaseState
import com.biomech.core.mvi.BaseViewModel
import com.biomech.core.network.api.PredictStreamClient
import com.biomech.core.network.api.StreamSample
import com.biomech.domain.model.EMGSample
import com.biomech.domain.usecase.StartEMGSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val deviceConnected: Boolean = false,
    val emgData: List<Float> = emptyList(),
    val isRecording: Boolean = false,
    val predictionLabel: String? = null,
    val streamConnected: Boolean = false,
    val prostheticConnected: Boolean = false,
    val prostheticMovement: String = "",
) : BaseState

sealed class DashboardAction : BaseAction {
    data object StartRecording : DashboardAction()
    data object StopRecording : DashboardAction()
    data class SendProstheticCommand(val command: ProstheticCommand) : DashboardAction()
}

sealed class DashboardEvent : BaseEvent

class DashboardViewModel(
    private val bleManager: BleManager,
    private val startSessionUseCase: StartEMGSessionUseCase,
) : BaseViewModel<DashboardState, DashboardAction, DashboardEvent>() {

    override val _state = MutableStateFlow(DashboardState())
    override val _event = Channel<DashboardEvent>(Channel.BUFFERED)

    private val predictClient = PredictStreamClient()
    private val buffer = mutableListOf<StreamSample>()
    private var streamJob: Job? = null

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
        scope.launch {
            bleManager.prostheticState.collect { state ->
                _state.value = _state.value.copy(
                    prostheticConnected = state.connected,
                    prostheticMovement = state.currentMovement,
                )
            }
        }
        scope.launch {
            predictClient.predictions.collect { prediction ->
                _state.value = _state.value.copy(predictionLabel = prediction)
            }
        }
    }

    override suspend fun handleAction(action: DashboardAction) {
        when (action) {
            DashboardAction.StartRecording -> {
                _state.value = _state.value.copy(isRecording = true)
                val connected = predictClient.connect(scope)
                _state.value = _state.value.copy(streamConnected = connected)
                if (connected) {
                    streamJob = scope.launch {
                        bleManager.emgDataStream.collect { sample ->
                            buffer.add(sample.toStreamSample())
                            if (buffer.size >= 32) {
                                predictClient.send(buffer.toList())
                                buffer.clear()
                            }
                        }
                    }
                }
            }
            DashboardAction.StopRecording -> {
                _state.value = _state.value.copy(isRecording = false)
                streamJob?.cancel()
                streamJob = null
                buffer.clear()
                predictClient.disconnect()
                _state.value = _state.value.copy(
                    streamConnected = false,
                    predictionLabel = null,
                )
            }
            is DashboardAction.SendProstheticCommand -> {
                bleManager.sendProstheticCommand(action.command)
            }
        }
    }
}

private fun EMGSample.toStreamSample() = StreamSample(
    ch1 = channel1, ch2 = channel2, ch3 = channel3, ch4 = channel4,
    ch5 = channel5, ch6 = channel6, ch7 = channel7, ch8 = channel8,
)
