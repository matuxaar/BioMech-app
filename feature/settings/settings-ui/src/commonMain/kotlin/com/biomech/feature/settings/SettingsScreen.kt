package com.biomech.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources

@Composable
fun SettingsScreen(
    onNavigateToServerConfig: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToPrediction: () -> Unit,
    onLogout: () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(AppResources.strings.logout) },
            text = { Text(AppResources.strings.logoutConfirmBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(AppResources.strings.logout)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(AppResources.strings.cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
                ) {
                    Text(
                        AppResources.strings.settings,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                SettingsRow(
                    title = AppResources.strings.serverConfiguration,
                    subtitle = AppResources.strings.apiUrlTest,
                    onClick = onNavigateToServerConfig,
                )
            }
            item {
                SettingsRow(
                    title = AppResources.strings.appearance,
                    subtitle = AppResources.strings.light.lowercase() + " / " + AppResources.strings.dark.lowercase(),
                    onClick = onNavigateToAppearance,
                )
            }
            item {
                SettingsRow(
                    title = AppResources.strings.language,
                    subtitle = "EN / RU",
                    onClick = onNavigateToLanguage,
                )
            }
            item {
                SettingsRow(
                    title = AppResources.strings.streaming,
                    subtitle = AppResources.strings.streamingDesc,
                    onClick = onNavigateToPrediction,
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(AppResources.strings.logout)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "›",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
