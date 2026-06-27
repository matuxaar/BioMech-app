package com.biomech.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.biomech.core.common.ThemeMode
import com.biomech.core.navigation.LocalNavigator
import com.biomech.core.navigation.Screen
import com.biomech.core.resource.AppResources
import com.biomech.core.resource.Locale
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType
import com.biomech.feature.devices.AddDeviceBottomSheet
import com.biomech.feature.devices.DeviceDetailSheet
import com.biomech.feature.devices.DevicesAction
import com.biomech.feature.devices.DevicesEvent
import com.biomech.feature.devices.DevicesViewModel
import com.biomech.feature.devices.EditDeviceBottomSheet
import com.biomech.core.common.currentTimeMillis
import com.biomech.feature.home.HomeAction
import com.biomech.feature.home.HomeViewModel
import com.biomech.feature.home.HomeScreen
import com.biomech.feature.home.RecordBottomSheet
import com.biomech.feature.home.RecordedFile
import com.biomech.feature.profile.ProfileAction
import com.biomech.feature.profile.ProfileEvent
import com.biomech.feature.profile.ProfileScreen
import com.biomech.feature.profile.ProfileViewModel
import com.biomech.feature.settings.AppearanceScreen
import com.biomech.feature.settings.LanguageScreen
import com.biomech.feature.settings.PredictionConfigScreen
import com.biomech.feature.settings.ServerConfigAction
import com.biomech.feature.settings.ServerConfigEvent
import com.biomech.feature.settings.ServerConfigScreen
import com.biomech.feature.settings.ServerConfigViewModel
import com.biomech.feature.settings.SettingsAction
import com.biomech.feature.settings.SettingsEvent
import com.biomech.feature.settings.SettingsScreen
import com.biomech.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class BottomTab { HOME, PROFILE, SETTINGS }

enum class SettingsSubScreen { SERVER_CONFIG, APPEARANCE, LANGUAGE, PREDICTION }

private val offlineDevices = listOf(
    Device(id = "dev-1", name = "MyoBand Pro", type = DeviceType.SENSOR, hwVersion = "2.1.0"),
    Device(id = "dev-2", name = "NeuroFlex", type = DeviceType.SENSOR, hwVersion = "1.4.2"),
    Device(id = "dev-3", name = "BioPulse", type = DeviceType.SENSOR, hwVersion = "3.0.1"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isOffline: Boolean = false,
    onConnectionRestored: (() -> Unit)? = null,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChanged: (ThemeMode) -> Unit = {},
    useGridLayout: Boolean = true,
    onGridLayoutChanged: (Boolean) -> Unit = {},
    locale: Locale = Locale.EN,
    onLocaleChanged: (Locale) -> Unit = {},
) {
    val navigator = LocalNavigator.current
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showAddDeviceSheet by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<Device?>(null) }
    var showEditSheet by remember { mutableStateOf(false) }
    var editingDevice by remember { mutableStateOf<Device?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingDevice by remember { mutableStateOf<Device?>(null) }
    var settingsSubScreen by remember { mutableStateOf<SettingsSubScreen?>(null) }
    var showRecordSheet by remember { mutableStateOf(false) }
    var recordingDevice by remember { mutableStateOf<Device?>(null) }
    var savedRecordings by remember { mutableStateOf(listOf<RecordedFile>()) }

    val homeViewModel: HomeViewModel = koinInject()
    val homeState by homeViewModel.state.collectAsState()

    val profileViewModel: ProfileViewModel = koinInject()
    val profileState by profileViewModel.state.collectAsState()
    val profileScope = rememberCoroutineScope()

    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsState()
    val settingsScope = rememberCoroutineScope()

    val serverConfigViewModel: ServerConfigViewModel = koinInject()
    val serverConfigState by serverConfigViewModel.state.collectAsState()

    val devicesViewModel: DevicesViewModel = koinInject()
    val devicesState by devicesViewModel.state.collectAsState()

    var streamingEnabled by remember { mutableStateOf(false) }
    var streamingConnected by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isOffline) {
            homeViewModel.dispatch(HomeAction.LoadDevices)
            profileViewModel.dispatch(ProfileAction.LoadProfile)
        }
    }

    LaunchedEffect(Unit) {
        launch {
            profileViewModel.event.collect { event ->
                when (event) {
                    ProfileEvent.NavigateToLogin -> navigator.navigateAndClear(Screen.Login)
                }
            }
        }
        launch {
            settingsViewModel.event.collect { event ->
                when (event) {
                    SettingsEvent.NavigateToLogin -> navigator.navigateAndClear(Screen.Login)
                    SettingsEvent.ConnectionRestored -> onConnectionRestored?.invoke()
                }
            }
        }
        launch {
            devicesViewModel.event.collect { event ->
                when (event) {
                    DevicesEvent.DeviceCreated -> {
                        showAddDeviceSheet = false
                        homeViewModel.dispatch(HomeAction.LoadDevices)
                    }
                    DevicesEvent.DeviceUpdated -> {
                        showEditSheet = false
                        editingDevice = null
                        homeViewModel.dispatch(HomeAction.LoadDevices)
                    }
                    DevicesEvent.DeviceDeleted -> {
                        deletingDevice = null
                        homeViewModel.dispatch(HomeAction.LoadDevices)
                    }
                }
            }
        }
        launch {
            serverConfigViewModel.event.collect { event ->
                when (event) {
                    ServerConfigEvent.NavigateBack -> settingsSubScreen = null
                    ServerConfigEvent.ConnectionRestored -> {
                        settingsSubScreen = null
                        onConnectionRestored?.invoke()
                    }
                }
            }
        }
    }

    if (showAddDeviceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddDeviceSheet = false }
        ) {
            AddDeviceBottomSheet(
                scannedDevices = devicesState.scannedDevices,
                isScanning = devicesState.isScanning,
                isCreating = devicesState.isCreating,
                createError = devicesState.createError,
                onStartScan = { devicesViewModel.dispatch(DevicesAction.StartScan) },
                onStopScan = { devicesViewModel.dispatch(DevicesAction.StopScan) },
                onConnect = { deviceId ->
                    devicesViewModel.dispatch(DevicesAction.Connect(deviceId))
                    showAddDeviceSheet = false
                },
                onCreateDevice = { name, type, hwVersion ->
                    devicesViewModel.dispatch(DevicesAction.CreateDevice(name, type, hwVersion))
                },
                onDismiss = { showAddDeviceSheet = false },
            )
        }
    }

    selectedDevice?.let { device ->
        ModalBottomSheet(
            onDismissRequest = { selectedDevice = null }
        ) {
            DeviceDetailSheet(
                device = device,
                onDismiss = { selectedDevice = null },
            )
        }
    }

    editingDevice?.let { device ->
        if (showEditSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showEditSheet = false
                    editingDevice = null
                }
            ) {
                EditDeviceBottomSheet(
                    device = device,
                    isUpdating = devicesState.isUpdating,
                    updateError = devicesState.updateError,
                    onUpdateDevice = { name, hwVersion ->
                        devicesViewModel.dispatch(DevicesAction.UpdateDevice(
                            id = device.id,
                            name = name,
                            hwVersion = hwVersion,
                            type = null,
                        ))
                    },
                    onDismiss = {
                        showEditSheet = false
                        editingDevice = null
                    },
                )
            }
        }
    }

    deletingDevice?.let { device ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    deletingDevice = null
                },
                title = { Text(AppResources.strings.deleteDevice) },
                text = { Text(AppResources.strings.deleteDeviceConfirm(device.name)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            devicesViewModel.dispatch(DevicesAction.DeleteDevice(device.id))
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                    ) {
                        Text(AppResources.strings.delete)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        deletingDevice = null
                    }) {
                        Text(AppResources.strings.cancel)
                    }
                },
            )
        }
    }

    recordingDevice?.let { device ->
        if (showRecordSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showRecordSheet = false
                    recordingDevice = null
                },
            ) {
                RecordBottomSheet(
                    device = device,
                    savedRecordings = savedRecordings.filter { it.deviceId == device.id },
                    onDismiss = {
                        showRecordSheet = false
                        recordingDevice = null
                    },
                    onSave = { label, csvBytes ->
                        val file = RecordedFile(
                            fileName = label,
                            csvBytes = csvBytes,
                            deviceId = device.id,
                            deviceName = device.name,
                            label = label,
                            sampleCount = csvBytes.size / 50,
                            timestamp = currentTimeMillis(),
                        )
                        savedRecordings = savedRecordings + file
                    },
                    onDownload = { file ->
                        // TODO: actual file export — save to external storage
                    },
                )
            }
        }
    }

    Scaffold(
        topBar = {
            if (isOffline) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTab = BottomTab.SETTINGS },
                ) {
                    Text(
                        text = AppResources.strings.offlineTap,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        },
        bottomBar = {}
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Crossfade(targetState = selectedTab) { tab ->
                when (tab) {
                BottomTab.HOME -> {
                    HomeScreen(
                        devices = if (isOffline) offlineDevices else homeState.devices,
                        useGridLayout = useGridLayout,
                        onAddDevice = { showAddDeviceSheet = true },
                        onDeviceClick = { device -> selectedDevice = device },
                        onNavigateToTraining = { navigator.navigateTo(Screen.Training) },
                        onEditDevice = { device ->
                            editingDevice = device
                            showEditSheet = true
                        },
                        onDeleteDevice = { device ->
                            deletingDevice = device
                            showDeleteDialog = true
                        },
                        onRecord = { device ->
                            recordingDevice = device
                            showRecordSheet = true
                        },
                    )
                }
                BottomTab.PROFILE -> {
                    ProfileScreen(
                        email = if (isOffline) "demo@biomech.app" else profileState.email,
                        nickname = profileState.nickname,
                        photoUrl = profileState.photoUrl,
                        deviceCount = if (isOffline) offlineDevices.size else profileState.deviceCount,
                        completedTrainings = profileState.completedTrainings,
                        averageAccuracy = profileState.averageAccuracy,
                        topMovements = profileState.topMovements,
                        isUpdating = profileState.isUpdating,
                        updateError = profileState.updateError,
                        onUpdateNickname = { nickname ->
                            profileViewModel.dispatch(ProfileAction.UpdateNickname(nickname))
                        },
                        onUploadAvatar = { bytes, fileName ->
                            profileViewModel.dispatch(ProfileAction.UploadAvatar(bytes, fileName))
                        },
                    )
                }
                BottomTab.SETTINGS -> {
                    when (settingsSubScreen) {
                        SettingsSubScreen.SERVER_CONFIG -> ServerConfigScreen(
                            serverUrl = serverConfigState.serverUrl,
                            connectionStatus = serverConfigState.connectionStatus,
                            onServerUrlChange = { serverConfigViewModel.dispatch(ServerConfigAction.UpdateServerUrl(it)) },
                            onTestConnection = { serverConfigViewModel.dispatch(ServerConfigAction.TestConnection) },
                            onBack = { serverConfigViewModel.dispatch(ServerConfigAction.GoBack) },
                        )
                        SettingsSubScreen.APPEARANCE -> AppearanceScreen(
                            themeMode = themeMode,
                            onThemeModeChanged = onThemeModeChanged,
                            useGridLayout = useGridLayout,
                            onGridLayoutChanged = onGridLayoutChanged,
                            onBack = { settingsSubScreen = null },
                        )
                        SettingsSubScreen.LANGUAGE -> LanguageScreen(
                            locale = locale,
                            onLocaleChanged = onLocaleChanged,
                            onBack = { settingsSubScreen = null },
                        )
                        SettingsSubScreen.PREDICTION -> PredictionConfigScreen(
                            streamingEnabled = streamingEnabled,
                            streamingConnected = streamingConnected,
                            onToggleStreaming = { enabled ->
                                streamingEnabled = enabled
                            },
                            onBack = { settingsSubScreen = null },
                        )
                        null -> SettingsScreen(
                            onNavigateToServerConfig = { settingsSubScreen = SettingsSubScreen.SERVER_CONFIG },
                            onNavigateToAppearance = { settingsSubScreen = SettingsSubScreen.APPEARANCE },
                            onNavigateToLanguage = { settingsSubScreen = SettingsSubScreen.LANGUAGE },
                            onNavigateToPrediction = { settingsSubScreen = SettingsSubScreen.PREDICTION },
                            onLogout = {
                                settingsViewModel.dispatch(SettingsAction.Logout)
                            },
                        )
                    }
                }
            }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.7f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BottomTabItem(
                            selected = selectedTab == BottomTab.HOME,
                            onClick = { selectedTab = BottomTab.HOME },
                            emoji = "🏠",
                            label = AppResources.strings.home,
                        )
                        BottomTabItem(
                            selected = selectedTab == BottomTab.PROFILE,
                            onClick = { selectedTab = BottomTab.PROFILE },
                            emoji = "👤",
                            label = AppResources.strings.profile,
                        )
                        BottomTabItem(
                            selected = selectedTab == BottomTab.SETTINGS,
                            onClick = { selectedTab = BottomTab.SETTINGS },
                            emoji = "⚙️",
                            label = AppResources.strings.settings,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    emoji: String,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                else Color.Transparent
            )
            .padding(horizontal = 4.dp, vertical = 3.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
