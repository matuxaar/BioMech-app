package com.biomech.core.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import com.biomech.domain.model.EMGSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _characteristicValue = MutableStateFlow(BleCharacteristicValue("", ByteArray(0)))
    override val characteristicValueStream = _characteristicValue.asStateFlow()

    private var gatt: BluetoothGatt? = null
    private var writeCharUuid: String? = null
    private val cccdUuid = "00002902-0000-1000-8000-00805f9b34fb"

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            val device = result.device ?: return
            val name = device.name ?: "Unknown"
            val existing = _scannedDevices.value.toMutableList()
            existing.removeAll { it.id == device.address }
            existing.add(BleDevice(device.address, name, result.rssi))
            _scannedDevices.value = existing
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun startScanning() {
        _scannedDevices.value = emptyList()
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
    override suspend fun connect(
        deviceId: String,
        serviceUuid: String,
        notifyCharUuids: List<String>,
        writeCharUuid: String?,
    ) {
        this.writeCharUuid = writeCharUuid
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
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val targetService = UUID.fromString(serviceUuid)
                val service = gatt.getService(targetService) ?: return
                for (char in service.characteristics) {
                    if (char.uuid.toString() in notifyCharUuids) {
                        gatt.setCharacteristicNotification(char, true)
                        val descriptor = char.getDescriptor(UUID.fromString(cccdUuid))
                        descriptor?.let {
                            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(it)
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val data = characteristic.value ?: return
                _characteristicValue.value = BleCharacteristicValue(characteristic.uuid.toString(), data)

                // auto-parse 8-byte EMG samples (16 bytes = 8x int16)
                if (data.size >= 16) {
                    val buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
                    _emgDataStream.value = EMGSample(
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
    override suspend fun writeCharacteristic(charUuid: String, data: ByteArray) {
        val gatt = gatt ?: return
        for (service in gatt.services) {
            val char = service.getCharacteristic(UUID.fromString(charUuid)) ?: continue
            char.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.writeCharacteristic(char, data, BluetoothGatt.GATT_SUCCESS)
            return
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun discoverCharacteristics(deviceId: String): List<BleCharacteristicInfo> {
        val device = bluetoothAdapter.getRemoteDevice(deviceId)
        var result = listOf<BleCharacteristicInfo>()
        val lock = Object()

        val tempGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val chars = mutableListOf<BleCharacteristicInfo>()
                for (service in gatt.services) {
                    for (char in service.characteristics) {
                        chars.add(BleCharacteristicInfo(
                            serviceUuid = service.uuid.toString(),
                            charUuid = char.uuid.toString(),
                            supportsNotify = (char.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0,
                            supportsWrite = (char.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0,
                        ))
                    }
                }
                result = chars
                gatt.disconnect()
                gatt.close()
                synchronized(lock) { lock.notify() }
            }
        })

        synchronized(lock) { lock.wait(10_000) }
        return result
    }
}
