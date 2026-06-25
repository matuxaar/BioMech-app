package com.biomech.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType

private fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PROSTHETIC -> "\uD83E\uDDBE"
    DeviceType.SENSOR -> "\u2699\uFE0F"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    devices: List<Device>,
    onAddDevice: () -> Unit,
    onDeviceClick: (Device) -> Unit,
    onEditDevice: ((Device) -> Unit)? = null,
    onDeleteDevice: ((Device) -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
                ) {
                    Text(
                        "My Devices",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "add_device") {
                Card(
                    onClick = onAddDevice,
                    modifier = Modifier.aspectRatio(1f),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("+", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "New Device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            items(devices, key = { it.id }) { device ->
                var showMenu by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .combinedClickable(
                            onClick = { onDeviceClick(device) },
                            onLongClick = { showMenu = true },
                        ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                deviceEmoji(device.type),
                                fontSize = 36.sp,
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                device.name,
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                device.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "v${device.hwVersion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEditDevice?.invoke(device)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDeleteDevice?.invoke(device)
                        },
                    )
                }
            }
        }
    }
}
