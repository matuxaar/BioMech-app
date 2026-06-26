package com.biomech.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources

@Composable
fun ServerConfigScreen(
    serverUrl: String,
    connectionStatus: ConnectionStatus = ConnectionStatus.Idle,
    onServerUrlChange: (String) -> Unit,
    onTestConnection: () -> Unit,
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
                    TextButton(onClick = onBack) {
                        Text(AppResources.strings.back)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        AppResources.strings.serverConfigTitle,
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
            Text(
                text = AppResources.strings.serverConfigDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = serverUrl,
                onValueChange = onServerUrlChange,
                label = { Text(AppResources.strings.apiUrl) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = onTestConnection,
                modifier = Modifier.fillMaxWidth(),
                enabled = connectionStatus != ConnectionStatus.Testing,
            ) {
                if (connectionStatus == ConnectionStatus.Testing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(AppResources.strings.testConnection)
                }
            }

            when (connectionStatus) {
                ConnectionStatus.Success -> {
                    Text(
                        AppResources.strings.connected,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                ConnectionStatus.Failed -> {
                    Text(
                        AppResources.strings.connectionFailed,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                else -> {}
            }
        }
    }
}
