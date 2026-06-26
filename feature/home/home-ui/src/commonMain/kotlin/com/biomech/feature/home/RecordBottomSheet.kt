package com.biomech.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.Device
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun RecordBottomSheet(
    device: Device,
    onDismiss: () -> Unit,
    onSave: (label: String, csvBytes: ByteArray) -> Unit,
) {
    var isRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var label by remember { mutableStateOf("") }
    var sampleCount by remember { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000)
                elapsedSeconds++
                sampleCount = elapsedSeconds * 100
            }
        }
    }

    fun formatTime(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            AppResources.strings.record,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            device.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))

        if (isRecording) {
            Text(
                AppResources.strings.recordingLabel,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                formatTime(elapsedSeconds),
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "$sampleCount samples",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (elapsedSeconds > 0) {
            Text(
                AppResources.strings.recordingComplete,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "${formatTime(elapsedSeconds)}  |  $sampleCount samples",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(24.dp))

        if (!isRecording && elapsedSeconds > 0) {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text(AppResources.strings.saveRecording) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val csv = generateCsv(device.id, sampleCount)
                    val name = label.ifBlank { "recording_${elapsedSeconds}s.csv" }
                    onSave(name, csv.toByteArray())
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(AppResources.strings.upload)
            }
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (isRecording) {
                    isRecording = false
                } else {
                    isRecording = true
                    elapsedSeconds = 0
                    sampleCount = 0
                    label = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isRecording) ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ) else ButtonDefaults.buttonColors(),
        ) {
            Text(if (isRecording) AppResources.strings.stop else AppResources.strings.startRecording)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onDismiss) {
            Text(AppResources.strings.cancel)
        }
    }
}

private fun generateCsv(deviceId: String, sampleCount: Int): String {
    val sb = StringBuilder()
    sb.appendLine("sample,ch1,ch2,ch3,ch4,ch5,ch6,ch7,ch8,timestamp_ms")
    for (i in 0 until sampleCount) {
        val values = (1..8).map { formatDouble3(Random.nextDouble() * 3.3) }
        val ts = i * 10
        sb.appendLine("$i,${values.joinToString(",")},$ts")
    }
    return sb.toString()
}

private fun formatDouble3(value: Double): String {
    val rounded = (value * 1000).toLong()
    val intPart = rounded / 1000
    val fracPart = (rounded % 1000).toString().padStart(3, '0')
    return "$intPart.$fracPart"
}
