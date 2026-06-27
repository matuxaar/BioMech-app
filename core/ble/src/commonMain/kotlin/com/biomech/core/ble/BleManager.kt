package com.biomech.core.ble

import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.Flow

interface BleManager {
    val scannedDevices: Flow<List<BleDevice>>
    val emgDataStream: Flow<EMGSample>
    val connectionState: Flow<ConnectionState>
    val prostheticState: Flow<ProstheticState>

    suspend fun startScanning()
    suspend fun stopScanning()
    suspend fun connect(deviceId: String)
    suspend fun disconnect()
    suspend fun sendProstheticCommand(command: ProstheticCommand)
}

data class BleDevice(
    val id: String,
    val name: String,
    val rssi: Int,
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCONNECTING
}

data class ProstheticState(
    val connected: Boolean = false,
    val currentMovement: String = "",
    val batteryLevel: Int = 0,
)

enum class ProstheticCommand(val code: Byte, val label: String) {
    HAND_OPEN(0x01, "Hand Open"),
    HAND_CLOSE(0x02, "Hand Close"),
    WRIST_FLEX(0x03, "Wrist Flex"),
    WRIST_EXTEND(0x04, "Wrist Extend"),
    WRIST_ROTATE_CW(0x05, "Wrist Rotate CW"),
    WRIST_ROTATE_CCW(0x06, "Wrist Rotate CCW"),
    STOP(0x00, "Stop"),
}
