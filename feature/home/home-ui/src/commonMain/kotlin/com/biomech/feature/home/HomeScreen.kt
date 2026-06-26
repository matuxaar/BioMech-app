package com.biomech.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.biomech.core.resource.AppResources
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType

private fun deviceEmoji(type: DeviceType): String = when (type) {
    DeviceType.PROSTHETIC -> "\uD83E\uDDBE"
    DeviceType.SENSOR -> "\u2699\uFE0F"
}

private enum class MenuAction { TRAINING, RECORD, EDIT, DELETE }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    devices: List<Device>,
    useGridLayout: Boolean = true,
    onAddDevice: () -> Unit,
    onDeviceClick: (Device) -> Unit,
    onEditDevice: ((Device) -> Unit)? = null,
    onDeleteDevice: ((Device) -> Unit)? = null,
    onNavigateToTraining: ((Device) -> Unit)? = null,
    onRecord: ((Device) -> Unit)? = null,
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
                        AppResources.strings.myDevices,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                HorizontalDivider()
            }
        },
    ) { padding ->
        if (useGridLayout) {
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
                    GridAddCard(onAddDevice = onAddDevice)
                }
                items(devices, key = { it.id }) { device ->
                    GridDeviceCard(
                        device = device,
                        onDeviceClick = onDeviceClick,
                        onEditDevice = onEditDevice,
                        onDeleteDevice = onDeleteDevice,
                        onNavigateToTraining = onNavigateToTraining,
                        onRecord = onRecord,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "add_device") {
                    ListAddCard(onAddDevice = onAddDevice)
                }
                items(devices, key = { it.id }) { device ->
                    ListDeviceCard(
                        device = device,
                        onDeviceClick = onDeviceClick,
                        onEditDevice = onEditDevice,
                        onDeleteDevice = onDeleteDevice,
                        onNavigateToTraining = onNavigateToTraining,
                        onRecord = onRecord,
                    )
                }
            }
        }
    }
}

@Composable
private fun GridAddCard(onAddDevice: () -> Unit) {
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
                    AppResources.strings.newDevice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ListAddCard(onAddDevice: () -> Unit) {
    Card(onClick = onAddDevice) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("+", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                AppResources.strings.addNewDevice,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private fun menuActionsForDevice(device: Device): List<Pair<MenuAction, String>> {
    return when (device.type) {
        DeviceType.PROSTHETIC -> listOf(
            MenuAction.TRAINING to "\uD83C\uDFAF",
            MenuAction.EDIT to "\u270F\uFE0F",
            MenuAction.DELETE to "\uD83D\uDDD1\uFE0F",
        )
        DeviceType.SENSOR -> listOf(
            MenuAction.RECORD to "\u23FA",
            MenuAction.EDIT to "\u270F\uFE0F",
            MenuAction.DELETE to "\uD83D\uDDD1\uFE0F",
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridDeviceCard(
    device: Device,
    onDeviceClick: (Device) -> Unit,
    onEditDevice: ((Device) -> Unit)?,
    onDeleteDevice: ((Device) -> Unit)?,
    onNavigateToTraining: ((Device) -> Unit)?,
    onRecord: ((Device) -> Unit)?,
) {
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
                Text(deviceEmoji(device.type), fontSize = 36.sp)
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
                    AppResources.strings.deviceTypeLabel(device.type.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Text(
                    AppResources.strings.version(device.hwVersion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    if (showMenu) {
        val density = LocalDensity.current
        val items = menuActionsForDevice(device)
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
                items.forEach { (action, emoji) ->
                    val onClick: () -> Unit = {
                        showMenu = false
                        when (action) {
                            MenuAction.TRAINING -> onNavigateToTraining?.invoke(device)
                            MenuAction.RECORD -> onRecord?.invoke(device)
                            MenuAction.EDIT -> onEditDevice?.invoke(device)
                            MenuAction.DELETE -> onDeleteDevice?.invoke(device)
                        }
                    }
                    ActionCard(
                        emoji = emoji,
                        text = when (action) {
                            MenuAction.TRAINING -> AppResources.strings.training
                            MenuAction.RECORD -> AppResources.strings.record
                            MenuAction.EDIT -> AppResources.strings.edit
                            MenuAction.DELETE -> AppResources.strings.delete
                        },
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListDeviceCard(
    device: Device,
    onDeviceClick: (Device) -> Unit,
    onEditDevice: ((Device) -> Unit)?,
    onDeleteDevice: ((Device) -> Unit)?,
    onNavigateToTraining: ((Device) -> Unit)?,
    onRecord: ((Device) -> Unit)?,
) {
    var showMenu by remember { mutableStateOf(false) }
    var cardPosition by remember { mutableStateOf(Offset.Zero) }
    var cardWidth by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                cardPosition = coords.positionInParent()
                cardWidth = coords.size.width.toFloat()
            }
            .combinedClickable(
                onClick = { onDeviceClick(device) },
                onLongClick = { showMenu = true },
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(deviceEmoji(device.type), fontSize = 32.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    device.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    AppResources.strings.deviceTypeLabel(device.type.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    AppResources.strings.version(device.hwVersion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showMenu) {
        val density = LocalDensity.current
        val items = menuActionsForDevice(device)
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
                items.forEach { (action, emoji) ->
                    val onClick: () -> Unit = {
                        showMenu = false
                        when (action) {
                            MenuAction.TRAINING -> onNavigateToTraining?.invoke(device)
                            MenuAction.RECORD -> onRecord?.invoke(device)
                            MenuAction.EDIT -> onEditDevice?.invoke(device)
                            MenuAction.DELETE -> onDeleteDevice?.invoke(device)
                        }
                    }
                    ActionCard(
                        emoji = emoji,
                        text = when (action) {
                            MenuAction.TRAINING -> AppResources.strings.training
                            MenuAction.RECORD -> AppResources.strings.record
                            MenuAction.EDIT -> AppResources.strings.edit
                            MenuAction.DELETE -> AppResources.strings.delete
                        },
                        onClick = onClick,
                    )
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
