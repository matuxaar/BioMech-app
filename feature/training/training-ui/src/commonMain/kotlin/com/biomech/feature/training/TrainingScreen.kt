package com.biomech.feature.training

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.Device
import com.biomech.domain.model.EMGSession
import com.biomech.domain.model.TrainingFile
import com.biomech.domain.model.TrainingJob
import com.biomech.domain.model.TrainingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    jobs: List<TrainingJob>,
    sessions: List<EMGSession>,
    selectedSessionIds: Set<String>,
    isCreating: Boolean = false,
    error: String? = null,
    files: List<TrainingFile> = emptyList(),
    isUploading: Boolean = false,
    uploadError: String? = null,
    selectedTab: Int = 0,
    devices: List<Device> = emptyList(),
    selectedDeviceId: String? = null,
    uploadLabel: String = "",
    onBack: () -> Unit = {},
    onToggleSession: (String) -> Unit,
    onStartTraining: () -> Unit,
    onSelectTab: (Int) -> Unit = {},
    onDeleteFile: (String) -> Unit = {},
    onUploadClick: () -> Unit = {},
    onSelectDevice: (String?) -> Unit = {},
    onUploadLabelChange: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppResources.strings.trainingTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onSelectTab(0) },
                    text = { Text(AppResources.strings.sessions) },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onSelectTab(1) },
                    text = { Text(AppResources.strings.files) },
                )
            }

            when (selectedTab) {
                0 -> SessionsTab(
                    sessions = sessions,
                    selectedSessionIds = selectedSessionIds,
                    isCreating = isCreating,
                    error = error,
                    jobs = jobs,
                    onToggleSession = onToggleSession,
                    onStartTraining = onStartTraining,
                )
                1 -> FilesTab(
                    files = files,
                    isUploading = isUploading,
                    uploadError = uploadError,
                    devices = devices,
                    selectedDeviceId = selectedDeviceId,
                    uploadLabel = uploadLabel,
                    onUploadLabelChange = onUploadLabelChange,
                    onDeleteFile = onDeleteFile,
                    onUploadClick = onUploadClick,
                    onSelectDevice = onSelectDevice,
                )
            }
        }
    }
}

@Composable
private fun SessionsTab(
    sessions: List<EMGSession>,
    selectedSessionIds: Set<String>,
    isCreating: Boolean,
    error: String?,
    jobs: List<TrainingJob>,
    onToggleSession: (String) -> Unit,
    onStartTraining: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (sessions.isNotEmpty()) {
            Text(
                text = AppResources.strings.selectSessions,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            sessions.forEach { session ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = session.id in selectedSessionIds,
                        onCheckedChange = { onToggleSession(session.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(session.label)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = onStartTraining,
                enabled = selectedSessionIds.isNotEmpty() && !isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(AppResources.strings.startTraining)
                }
            }
        }

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    AppResources.strings.noSessions,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = AppResources.strings.trainingHistory,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        if (jobs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(AppResources.strings.noTrainingJobs)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(jobs) { job ->
                    JobCard(job = job)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilesTab(
    files: List<TrainingFile>,
    isUploading: Boolean,
    uploadError: String?,
    devices: List<Device> = emptyList(),
    selectedDeviceId: String? = null,
    uploadLabel: String = "",
    onUploadLabelChange: (String) -> Unit = {},
    onDeleteFile: (String) -> Unit,
    onUploadClick: () -> Unit,
    onSelectDevice: (String?) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isUploading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        if (uploadError != null) {
            Text(
                text = uploadError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }

        if (devices.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = devices.firstOrNull { it.id == selectedDeviceId }?.name
                        ?: devices.first().name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(AppResources.strings.devices) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    devices.forEach { device ->
                        DropdownMenuItem(
                            text = { Text(device.name) },
                            onClick = {
                                onSelectDevice(device.id)
                                expanded = false
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = uploadLabel,
            onValueChange = onUploadLabelChange,
            label = { Text(AppResources.strings.fileLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onUploadClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading,
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text(AppResources.strings.upload)
            }
        }
        Spacer(Modifier.height(12.dp))

        if (files.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    AppResources.strings.noFiles,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(files) { file ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.originalName,
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = AppResources.strings.fileSize(file.fileSize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (file.label.isNotBlank()) {
                                    Text(
                                        text = file.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    IconButton(onClick = { onDeleteFile(file.id) }) {
                                        Text("\uD83D\uDDD1\uFE0F")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(job: TrainingJob) {
    val chipColor = when (job.status) {
        TrainingStatus.COMPLETED -> Color(0xFF4CAF50)
        TrainingStatus.RUNNING -> Color(0xFF2196F3)
        TrainingStatus.FAILED -> Color(0xFFF44336)
        TrainingStatus.PENDING -> Color(0xFF9E9E9E)
    }
    val chipLabel = when (job.status) {
        TrainingStatus.COMPLETED -> "Completed"
        TrainingStatus.RUNNING -> "Running..."
        TrainingStatus.FAILED -> "Failed"
        TrainingStatus.PENDING -> "Pending"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Job ${job.id.take(8)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(chipColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = chipLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = chipColor,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (job.status == TrainingStatus.RUNNING) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
            }
            if (job.status == TrainingStatus.COMPLETED && job.accuracy > 0) {
                Text(
                    text = "Accuracy: ${(job.accuracy * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (job.status == TrainingStatus.FAILED && job.errorMessage.isNotBlank()) {
                Text(
                    text = job.errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (job.createdAt.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Created: ${job.createdAt.take(16).replace("T", " ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
