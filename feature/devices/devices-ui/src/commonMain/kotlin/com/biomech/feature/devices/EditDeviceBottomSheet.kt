package com.biomech.feature.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeviceBottomSheet(
    device: Device,
    isUpdating: Boolean,
    updateError: String?,
    onUpdateDevice: (name: String, hwVersion: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(device.name) }
    var hwVersion by remember { mutableStateOf(device.hwVersion) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            AppResources.strings.editDevice,
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(Modifier.height(20.dp))

        if (updateError != null) {
            Text(
                text = updateError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(AppResources.strings.deviceName) },
            singleLine = true,
            enabled = !isUpdating,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = hwVersion,
            onValueChange = { hwVersion = it },
            label = { Text(AppResources.strings.hardwareVersion) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled = !isUpdating,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                enabled = !isUpdating,
            ) {
                Text(AppResources.strings.cancel)
            }
            Button(
                onClick = { onUpdateDevice(name, hwVersion) },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && hwVersion.isNotBlank() && !isUpdating,
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(AppResources.strings.save)
                }
            }
        }
    }
}
