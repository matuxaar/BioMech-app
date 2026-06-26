package com.biomech.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType

private fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PROSTHETIC -> "\uD83E\uDDBE"
    DeviceType.SENSOR -> "\u2699\uFE0F"
}

private data class ActionItem(val emoji: String, val label: String)

private val menuActions = listOf(
    ActionItem("\uD83C\uDFAF", "Training"),
    ActionItem("\u270F\uFE0F", "Edit"),
    ActionItem("\uD83D\uDDD1\uFE0F", "Delete"),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    devices: List<Device>,
    onAddDevice: () -> Unit,
    onDeviceClick: (Device) -> Unit,
    onEditDevice: ((Device) -> Unit)? = null,
    onDeleteDevice: ((Device) -> Unit)? = null,
    onNavigateToTraining: ((Device) -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
                ) {
                    Text(
                        "My Devices",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "add_device") {
                Card(
                    onClick = onAddDevice,
                    modifier = Modifier.aspectRatio(1f),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("+", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "New Device",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            items(devices, key = { it.id }) { device ->
                var showMenu by remember { mutableStateOf(false) }
                var cardPosition by remember { mutableStateOf(Offset.Zero) }
                var cardWidth by remember { mutableStateOf(0f) }

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .onGloballyPositioned { coords ->
                            cardPosition = coords.positionInParent()
                            cardWidth = coords.size.width.toFloat()
                        }
                        .combinedClickable(
                            onClick = { onDeviceClick(device) },
                            onLongClick = { showMenu = true },
                        ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                deviceEmoji(device.type),
                                fontSize = 36.sp,
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                device.name,
                                style = MaterialTheme.typography.titleSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                device.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "v${device.hwVersion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                if (showMenu) {
                    val density = LocalDensity.current
                    Popup(
                        onDismissRequest = { showMenu = false },
                        properties = PopupProperties(focusable = true),
                        offset = IntOffset(
                            x = (cardPosition.x + cardWidth + with(density) { 4.dp.toPx() }).toInt(),
                            y = cardPosition.y.toInt(),
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.widthIn(min = 140.dp),
                        ) {
                            menuActions.forEach { action ->
                                val onClick: () -> Unit = {
                                    showMenu = false
                                    when (action.label) {
                                        "Training" -> onNavigateToTraining?.invoke(device)
                                        "Edit" -> onEditDevice?.invoke(device)
                                        "Delete" -> onDeleteDevice?.invoke(device)
                                        else -> Unit
                                    }
                                }
                                ActionCard(
                                    emoji = action.emoji,
                                    text = action.label,
                                    onClick = onClick,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    emoji: String,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
