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
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType

private data class Gesture(
    val name: String,
    val emoji: String,
    val accuracy: Double?,
)

private val sampleGestures = listOf(
    Gesture("Rest", "\u270B", 0.95),
    Gesture("Fist", "\u270A", 0.92),
    Gesture("Open", "\uD83D\uDD90\uFE0F", 0.88),
    Gesture("Pinch", "\uD83E\uDD1F", 0.75),
    Gesture("Point", "\u261D\uFE0F", 0.70),
)

private fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PROSTHETIC -> "\uD83E\uDDBE"
    DeviceType.SENSOR -> "\u2699\uFE0F"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailSheet(
    device: Device,
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
                "Device Details",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        Spacer(Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Name", device.name)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow("Type", device.type.name.lowercase().replaceFirstChar { it.uppercase() })
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow("Hardware Version", device.hwVersion)
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                DetailRow("ID", device.id.take(8) + "...")
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Trained Actions",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(12.dp))

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(sampleGestures) { gesture ->
                GestureCard(
                    gesture = gesture,
                    onClick = { /* TODO: call /api/v1/predict when endpoint is available */ },
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }
}

@Composable
private fun GestureCard(
    gesture: Gesture,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.height(80.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(gesture.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    gesture.name,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (gesture.accuracy != null) {
                    Text(
                        "${(gesture.accuracy * 100).toInt()}% accuracy",
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
