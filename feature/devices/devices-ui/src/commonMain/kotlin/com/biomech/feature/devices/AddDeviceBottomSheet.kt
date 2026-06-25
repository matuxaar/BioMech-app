package com.biomech.feature.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.biomech.core.ble.BleDevice

private val deviceTypes = listOf("sensor", "prosthetic")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceBottomSheet(
    scannedDevices: List<BleDevice>,
    isScanning: Boolean,
    isCreating: Boolean,
    createError: String?,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnect: (String) -> Unit,
    onCreateDevice: (name: String, type: String, hwVersion: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var mode by remember { mutableStateOf("ble") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Text(
            "Add New Device",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = mode == "ble",
                onClick = { mode = "ble" },
                label = { Text("BLE Scan") },
                modifier = Modifier.weight(1f),
            )
            FilterChip(
                selected = mode == "manual",
                onClick = { mode = "manual" },
                label = { Text("Manual") },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(16.dp))

        if (createError != null) {
            Text(
                text = createError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }

        when (mode) {
            "ble" -> BleScanContent(
                scannedDevices = scannedDevices,
                isScanning = isScanning,
                onStartScan = onStartScan,
                onStopScan = onStopScan,
                onConnect = onConnect,
            )
            "manual" -> ManualEntryContent(
                isCreating = isCreating,
                onCreateDevice = onCreateDevice,
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun BleScanContent(
    scannedDevices: List<BleDevice>,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnect: (String) -> Unit,
) {
    Button(
        onClick = { if (isScanning) onStopScan() else onStartScan() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (isScanning) "Stop Scanning" else "Start Scanning")
    }

    Spacer(Modifier.height(16.dp))

    if (scannedDevices.isEmpty() && !isScanning) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Press scan to find nearby devices",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(scannedDevices) { device ->
                Card(
                    onClick = { onConnect(device.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(device.name, style = MaterialTheme.typography.titleSmall)
                            Text(device.id, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${device.rssi} dBm")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualEntryContent(
    isCreating: Boolean,
    onCreateDevice: (name: String, type: String, hwVersion: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(deviceTypes.first()) }
    var typeExpanded by remember { mutableStateOf(false) }
    var hwVersion by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Device Name") },
        singleLine = true,
        enabled = !isCreating,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    ExposedDropdownMenuBox(
        expanded = typeExpanded,
        onExpandedChange = { typeExpanded = it },
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            enabled = !isCreating,
        )
        ExposedDropdownMenu(
            expanded = typeExpanded,
            onDismissRequest = { typeExpanded = false },
        ) {
            deviceTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        selectedType = type
                        typeExpanded = false
                    },
                )
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    OutlinedTextField(
        value = hwVersion,
        onValueChange = { hwVersion = it },
        label = { Text("Hardware Version") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        enabled = !isCreating,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = { onCreateDevice(name, selectedType, hwVersion) },
        modifier = Modifier.fillMaxWidth(),
        enabled = name.isNotBlank() && hwVersion.isNotBlank() && !isCreating,
    ) {
        if (isCreating) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
            )
            Spacer(Modifier.width(8.dp))
        }
        Text("Add Device")
    }
}
