package com.biomech.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources

@Composable
fun PredictionConfigScreen(
    streamingEnabled: Boolean = false,
    streamingConnected: Boolean = false,
    onToggleStreaming: (Boolean) -> Unit = {},
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        AppResources.strings.streaming,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppResources.strings.enableStreaming,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (streamingConnected)
                                    AppResources.strings.streamingConnected
                                else
                                    AppResources.strings.streamingDisconnected,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (streamingConnected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Switch(
                            checked = streamingEnabled,
                            onCheckedChange = onToggleStreaming,
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppResources.strings.streamingDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "1. Enable the switch above to connect via WebSocket",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "2. EMG samples will be streamed every ~32 samples",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "3. Predicted gestures will appear in the dashboard",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
