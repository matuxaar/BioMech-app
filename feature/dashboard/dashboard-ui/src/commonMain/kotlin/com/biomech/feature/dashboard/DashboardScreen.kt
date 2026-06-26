package com.biomech.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.component.EMGChart
import com.biomech.core.resource.AppResources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    deviceConnected: Boolean,
    emgData: List<Float>,
    predictionLabel: String?,
    streamConnected: Boolean,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNavigateToTraining: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppResources.strings.dashboardTitle) },
                actions = {
                    if (predictionLabel != null) {
                        Text(
                            text = predictionLabel,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    if (streamConnected) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "●",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    if (deviceConnected) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = AppResources.strings.connectedLabel,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppResources.strings.emgSignal,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        EMGChart(
                            data = emgData,
                            modifier = Modifier.height(200.dp)
                        )
                    }
                }
            }

            if (predictionLabel != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = AppResources.strings.predictionLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = predictionLabel,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onStartRecording,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(AppResources.strings.startRecording)
                    }
                    Button(
                        onClick = onStopRecording,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(AppResources.strings.stop)
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateToTraining,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(AppResources.strings.goToTraining)
                }
            }
        }
    }
}
