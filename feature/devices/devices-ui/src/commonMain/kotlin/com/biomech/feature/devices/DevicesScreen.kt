package com.biomech.feature.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.ble.BleDevice
import com.biomech.core.resource.AppResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    scannedDevices: List<BleDevice>,
    isScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onConnect: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(AppResources.strings.devices) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { if (isScanning) onStopScan() else onStartScan() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isScanning) AppResources.strings.stopScan else AppResources.strings.startScan)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (scannedDevices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(AppResources.strings.noDevicesFound, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
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
                                Text(AppResources.strings.rssi(device.rssi))
                            }
                        }
                    }
                }
            }
        }
    }
}
