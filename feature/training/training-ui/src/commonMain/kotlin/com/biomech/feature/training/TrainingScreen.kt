package com.biomech.feature.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.EMGSession
import com.biomech.domain.model.TrainingFile
import com.biomech.domain.model.TrainingJob

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
    onBack: () -> Unit = {},
    onToggleSession: (String) -> Unit,
    onStartTraining: () -> Unit,
    onSelectTab: (Int) -> Unit = {},
    onDeleteFile: (String) -> Unit = {},
    onUploadClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(AppResources.strings.trainingTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
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
            TabRow(selectedTabIndex = selectedTab) {
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
                    onDeleteFile = onDeleteFile,
                    onUploadClick = onUploadClick,
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = AppResources.strings.jobId(job.id),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(AppResources.strings.status(job.status.name))
                            if (job.accuracy > 0) {
                                Text(AppResources.strings.jobAccuracy((job.accuracy * 100).toInt()))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilesTab(
    files: List<TrainingFile>,
    isUploading: Boolean,
    uploadError: String?,
    onDeleteFile: (String) -> Unit,
    onUploadClick: () -> Unit,
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
                                }
                            }
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
