package com.biomech.feature.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceAction
import com.biomech.domain.model.DeviceType

@Composable
fun DeviceDetailSheet(
    device: Device,
    deviceActions: List<DeviceAction> = emptyList(),
    isLoadingActions: Boolean = false,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(deviceEmoji(device.type), fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                AppResources.strings.deviceDetails,
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        Spacer(Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(AppResources.strings.name, device.name)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(AppResources.strings.type, device.type.name.lowercase().replaceFirstChar { it.uppercase() })
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(AppResources.strings.hardwareVersion, device.hwVersion)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow(AppResources.strings.id, device.id.take(8) + "...")
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            AppResources.strings.trainedActions,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(12.dp))

        if (isLoadingActions) {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (deviceActions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No trained actions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(deviceActions) { action ->
                    GestureCard(
                        name = action.name,
                        emoji = action.emoji,
                        accuracy = action.accuracy,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(AppResources.strings.close)
        }
    }
}

private fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PROSTHETIC -> "\uD83E\uDDBE"
    DeviceType.SENSOR -> "\u2699\uFE0F"
}

@Composable
private fun GestureCard(
    name: String,
    emoji: String,
    accuracy: Double?,
) {
    Card(
        modifier = Modifier.height(80.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    name,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (accuracy != null) {
                    Text(
                        AppResources.strings.accuracy((accuracy * 100).toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
