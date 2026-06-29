package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class GesturePattern(
    val name: String,
    val channels: List<Float>,
)

private val REST = GesturePattern("REST", listOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f))
private val FIST = GesturePattern("FIST", listOf(0.85f, 0.80f, 0.75f, 0.70f, 0.15f, 0.10f, 0.10f, 0.05f))
private val OPEN = GesturePattern("OPEN", listOf(0.10f, 0.10f, 0.15f, 0.20f, 0.75f, 0.80f, 0.85f, 0.90f))
private val PINCH = GesturePattern("PINCH", listOf(0.70f, 0.65f, 0.20f, 0.15f, 0.65f, 0.70f, 0.20f, 0.10f))
private val POINT = GesturePattern("POINT", listOf(0.80f, 0.30f, 0.20f, 0.10f, 0.60f, 0.55f, 0.20f, 0.15f))

private val GESTURES = listOf(REST, FIST, OPEN, PINCH, POINT)

class SimulatedBleManager : BleManager {
    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices: Flow<List<BleDevice>> = _scannedDevices

    private val _emgDataStream = MutableSharedFlow<EMGSample>(replay = 0)
    override val emgDataStream: Flow<EMGSample> = _emgDataStream

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: Flow<ConnectionState> = _connectionState

    private val _characteristicValueStream = MutableSharedFlow<BleCharacteristicValue>(replay = 0)
    override val characteristicValueStream: Flow<BleCharacteristicValue> = _characteristicValueStream

    private var generationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var currentGesture = REST
    private var targetGesture = REST
    private var blendFactor = 1.0f
    private var sampleIndex = 0L
    private var gestureTimer = 0

    private val amplitudeScale = 1.65f
    private val noiseLevel = 0.04f

    override suspend fun startScanning() {
        _scannedDevices.value = listOf(
            BleDevice("sim-prosthetic-1", "Simulated Prosthetic v1", -45),
            BleDevice("sim-sensor-1", "Simulated Sensor v1", -52),
        )
    }

    override suspend fun stopScanning() {
        _scannedDevices.value = emptyList()
    }

    override suspend fun connect(
        deviceId: String,
        serviceUuid: String,
        notifyCharUuids: List<String>,
        writeCharUuid: String?,
    ) {
        _connectionState.value = ConnectionState.CONNECTING
        delay(800)
        _connectionState.value = ConnectionState.CONNECTED
        startEmgGeneration()
    }

    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        generationJob?.cancel()
        generationJob = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    override suspend fun writeCharacteristic(charUuid: String, data: ByteArray) {
        _characteristicValueStream.emit(BleCharacteristicValue(charUuid, data))
    }

    override suspend fun discoverCharacteristics(deviceId: String): List<BleCharacteristicInfo> {
        return listOf(
            BleCharacteristicInfo(
                serviceUuid = "0000fee0-0000-1000-8000-00805f9b34fb",
                charUuid = "0000fee1-0000-1000-8000-00805f9b34fb",
                supportsNotify = true,
                supportsWrite = false,
            ),
            BleCharacteristicInfo(
                serviceUuid = "0000fee0-0000-1000-8000-00805f9b34fb",
                charUuid = "0000fee2-0000-1000-8000-00805f9b34fb",
                supportsNotify = false,
                supportsWrite = true,
            ),
        )
    }

    private fun startEmgGeneration() {
        sampleIndex = 0L
        gestureTimer = 0
        currentGesture = REST
        targetGesture = REST
        blendFactor = 1.0f
        pickNextGesture()

        generationJob = scope.launch {
            while (isActive) {
                generateSample()
                sampleIndex++
                gestureTimer++
                updateGesture()
                delay(10L)
            }
        }
    }

    private fun pickNextGesture() {
        targetGesture = GESTURES.filter { it != currentGesture }.random()
        blendFactor = 0.0f
        gestureTimer = 0
    }

    private fun updateGesture() {
        if (targetGesture == currentGesture) return
        blendFactor = (blendFactor + 0.008f).coerceAtMost(1.0f)
        if (blendFactor >= 1.0f) {
            currentGesture = targetGesture
            gestureTimer = 0
            if (currentGesture != REST) {
                val holdTime = 200 + Random.nextInt(400)
                if (gestureTimer >= holdTime) pickNextGesture()
            }
        }
    }

    private suspend fun generateSample() {
        val noise = List(8) { (Random.nextFloat() - 0.5f) * 2f * noiseLevel }
        val tremor = List(8) { i ->
            val freq = 10.0 + i * 0.5
            val phase = i * PI / 4
            (sin(sampleIndex * freq * 0.001 + phase) * 0.03f).toFloat()
        }

        val channels = (0 until 8).map { i ->
            val base = lerp(currentGesture.channels[i], targetGesture.channels[i], blendFactor)
            val value = (base + noise[i] + tremor[i]) * amplitudeScale
            ((value * 1000).toLong() / 1000f).coerceIn(0f, 3.3f)
        }

        val sample = EMGSample(
            channel1 = channels[0], channel2 = channels[1], channel3 = channels[2], channel4 = channels[3],
            channel5 = channels[4], channel6 = channels[5], channel7 = channels[6], channel8 = channels[7],
        )
        _emgDataStream.emit(sample)
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
}
