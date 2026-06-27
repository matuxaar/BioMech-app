package com.biomech.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.biomech.core.image.BioMechAsyncImage
import com.biomech.core.image.rememberImagePickerLauncher
import com.biomech.core.network.ApiConfig
import com.biomech.core.resource.AppResources

@Composable
fun ProfileScreen(
    email: String,
    nickname: String = "",
    photoUrl: String = "",
    deviceCount: Int = 0,
    completedTrainings: Int = 0,
    averageAccuracy: Double = 0.0,
    topMovements: List<String> = emptyList(),
    isUpdating: Boolean = false,
    updateError: String? = null,
    onUpdateNickname: ((String) -> Unit)? = null,
    onUploadAvatar: ((ByteArray, String) -> Unit)? = null,
) {
    var editingNickname by remember(nickname) { mutableStateOf(nickname) }
    var showEditor by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    val pickImage = rememberImagePickerLauncher { bytes, fileName ->
        onUploadAvatar?.invoke(bytes, fileName)
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
                        AppResources.strings.profile,
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                val fullUrl = if (photoUrl.startsWith("http")) photoUrl
                    else {
                        val base = ApiConfig.baseUrl.trimEnd('/')
                        val host = base.substringBefore("/api").trimEnd('/')
                        host + photoUrl
                    }
                AvatarImage(fullUrl, nickname, 80.dp)
                if (onUploadAvatar != null) {
                    FilledIconButton(
                        onClick = { pickImage() },
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(4.dp),
                    ) {
                        Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (showEditor) {
                OutlinedTextField(
                    value = editingNickname,
                    onValueChange = { editingNickname = it },
                    label = { Text(AppResources.strings.nickname) },
                    singleLine = true,
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                if (updateError != null) {
                    Text(
                        updateError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showEditor = false; editingNickname = nickname },
                        enabled = !isUpdating,
                    ) {
                        Text(AppResources.strings.cancel)
                    }
                    Button(
                        onClick = { onUpdateNickname?.invoke(editingNickname) },
                        enabled = editingNickname.isNotBlank() && !isUpdating,
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(AppResources.strings.save)
                        }
                    }
                }
            } else {
                Text(
                    if (nickname.isNotBlank()) nickname else email,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                if (nickname.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { showEditor = true; editingNickname = nickname }) {
                    Text(AppResources.strings.editNickname)
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    value = deviceCount.toString(),
                    label = AppResources.strings.devices,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = completedTrainings.toString(),
                    label = AppResources.strings.trainings,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    value = "${(averageAccuracy * 100).toInt()}%",
                    label = "Accuracy",
                    modifier = Modifier.weight(1f),
                )
                if (topMovements.isNotEmpty()) {
                    StatCard(
                        value = topMovements.take(3).joinToString(", "),
                        label = "Top movements",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (topMovements.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            AppResources.strings.dashboard,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            AppResources.strings.dashboardDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarImage(photoUrl: String, fallback: String, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl.isNotBlank()) {
            BioMechAsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                (fallback.firstOrNull()?.uppercase() ?: "U"),
                fontSize = (size.value * 0.4).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
