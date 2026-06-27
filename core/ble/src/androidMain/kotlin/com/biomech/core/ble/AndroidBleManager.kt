package com.biomech.core.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class AndroidBleManager(private val context: Context) : BleManager {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    override val scannedDevices = _scannedDevices.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState = _connectionState.asStateFlow()

    private val _emgDataStream = MutableStateFlow(EMGSample(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
    override val emgDataStream = _emgDataStream.asStateFlow()

    private val _prostheticState = MutableStateFlow(ProstheticState())
    override val prostheticState = _prostheticState.asStateFlow()

    private var gatt: BluetoothGatt? = null
    private var scannedMap = mutableMapOf<String, BleDevice>()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            val device = result.device ?: return
            val name = device.name ?: "Unknown"
            if (name.startsWith("BioMech") || name.startsWith("Sensor") || name.startsWith("Prosthetic")) {
                scannedMap[device.address] = BleDevice(
                    id = device.address,
                    name = name,
                    rssi = result.rssi,
                )
                _scannedDevices.value = scannedMap.values.toList()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun startScanning() {
        scannedMap.clear()
        bluetoothLeScanner?.startScan(
            null,
            ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build(),
            scanCallback,
        )
    }

    @SuppressLint("MissingPermission")
    override suspend fun stopScanning() {
        bluetoothLeScanner?.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(deviceId: String) {
        _connectionState.value = ConnectionState.CONNECTING
        val device = bluetoothAdapter.getRemoteDevice(deviceId)
        gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        _connectionState.value = ConnectionState.CONNECTED
                        gatt.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        _connectionState.value = ConnectionState.DISCONNECTED
                        _prostheticState.value = _prostheticState.value.copy(connected = false)
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                findAndEnableNotifications(gatt)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val uuid = characteristic.uuid.toString()
                when (uuid) {
                    BleUuid.EMG_DATA -> {
                        val sample = parseEmgSample(characteristic.value)
                        if (sample != null) _emgDataStream.value = sample
                    }
                    BleUuid.PROSTHETIC_STATUS -> {
                        val value = characteristic.value
                        if (value.isNotEmpty()) {
                            _prostheticState.value = _prostheticState.value.copy(
                                connected = true,
                                currentMovement = value.first().toString(),
                            )
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override suspend fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTING
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    @SuppressLint("MissingPermission")
    override suspend fun sendProstheticCommand(command: ProstheticCommand) {
        val gatt = gatt ?: return
        val service = gatt.getService(UUID.fromString(BleUuid.PROSTHETIC_SERVICE))
            ?: return
        val characteristic = service.getCharacteristic(UUID.fromString(BleUuid.PROSTHETIC_COMMAND))
            ?: return
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        gatt.writeCharacteristic(characteristic, byteArrayOf(command.code), BluetoothGatt.GATT_SUCCESS)
    }

    private fun findAndEnableNotifications(gatt: BluetoothGatt) {
        for (service in gatt.services) {
            for (char in service.characteristics) {
                val uuid = char.uuid.toString()
                if (uuid == BleUuid.EMG_DATA || uuid == BleUuid.PROSTHETIC_STATUS) {
                    gatt.setCharacteristicNotification(char, true)
                    val descriptor = char.getDescriptor(
                        UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                    )
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }
                }
            }
        }
    }

    private fun parseEmgSample(data: ByteArray): EMGSample? {
        if (data.size < 16) return null
        val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        return EMGSample(
            channel1 = buf.getShort(0).toFloat(),
            channel2 = buf.getShort(2).toFloat(),
            channel3 = buf.getShort(4).toFloat(),
            channel4 = buf.getShort(6).toFloat(),
            channel5 = buf.getShort(8).toFloat(),
            channel6 = buf.getShort(10).toFloat(),
            channel7 = buf.getShort(12).toFloat(),
            channel8 = buf.getShort(14).toFloat(),
        )
    }
}
