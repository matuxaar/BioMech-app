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
import com.biomech.core.navigation.LocalNavigator
import com.biomech.core.navigation.Screen
import com.biomech.domain.model.Device
import com.biomech.domain.model.DeviceType
import com.biomech.feature.devices.AddDeviceBottomSheet
import com.biomech.feature.devices.DeviceDetailSheet
import com.biomech.feature.devices.DevicesAction
import com.biomech.feature.devices.DevicesEvent
import com.biomech.feature.devices.DevicesViewModel
import com.biomech.feature.home.HomeAction
import com.biomech.feature.home.HomeViewModel
import com.biomech.feature.home.HomeScreen
import com.biomech.feature.profile.ProfileAction
import com.biomech.feature.profile.ProfileEvent
import com.biomech.feature.profile.ProfileScreen
import com.biomech.feature.profile.ProfileViewModel
import com.biomech.feature.settings.SettingsAction
import com.biomech.feature.settings.SettingsEvent
import com.biomech.feature.settings.SettingsScreen
import com.biomech.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class BottomTab { HOME, PROFILE, SETTINGS }

private val offlineDevices = listOf(
    Device(id = "dev-1", name = "MyoBand Pro", type = DeviceType.SENSOR, hwVersion = "2.1.0"),
    Device(id = "dev-2", name = "NeuroFlex", type = DeviceType.SENSOR, hwVersion = "1.4.2"),
    Device(id = "dev-3", name = "BioPulse", type = DeviceType.SENSOR, hwVersion = "3.0.1"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(isOffline: Boolean = false) {
    val navigator = LocalNavigator.current
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showAddDeviceSheet by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("user@example.com") }
    var selectedDevice by remember { mutableStateOf<Device?>(null) }

    val homeViewModel: HomeViewModel = koinInject()
    val homeState by homeViewModel.state.collectAsState()

    val profileViewModel: ProfileViewModel = koinInject()
    val profileState by profileViewModel.state.collectAsState()
    val profileScope = rememberCoroutineScope()

    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsState()
    val settingsScope = rememberCoroutineScope()

    val devicesViewModel: DevicesViewModel = koinInject()
    val devicesState by devicesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (!isOffline) {
            homeViewModel.dispatch(HomeAction.LoadDevices)
            profileViewModel.dispatch(ProfileAction.LoadProfile)
        }
        profileViewModel.dispatch(ProfileAction.SetEmail(email))
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

    Scaffold(
        topBar = {
            if (isOffline) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Offline mode — server unavailable",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 20.dp)
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
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
                            label = "Home",
                        )
                        BottomTabItem(
                            selected = selectedTab == BottomTab.PROFILE,
                            onClick = { selectedTab = BottomTab.PROFILE },
                            emoji = "👤",
                            label = "Profile",
                        )
                        BottomTabItem(
                            selected = selectedTab == BottomTab.SETTINGS,
                            onClick = { selectedTab = BottomTab.SETTINGS },
                            emoji = "⚙️",
                            label = "Settings",
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Crossfade(targetState = selectedTab) { tab ->
                when (tab) {
                BottomTab.HOME -> {
                    HomeScreen(
                        devices = if (isOffline) offlineDevices else homeState.devices,
                        onAddDevice = { showAddDeviceSheet = true },
                        onDeviceClick = { device -> selectedDevice = device },
                        onNavigateToTraining = { navigator.navigateTo(Screen.Training) },
                    )
                }
                BottomTab.PROFILE -> {
                    ProfileScreen(
                        email = if (isOffline) "demo@biomech.app" else profileState.email,
                        deviceCount = (if (isOffline) offlineDevices else homeState.devices).size,
                    )
                }
                BottomTab.SETTINGS -> {
                    SettingsScreen(
                        serverUrl = settingsState.serverUrl,
                        onServerUrlChange = { settingsViewModel.dispatch(SettingsAction.UpdateServerUrl(it)) },
                        onLogout = {
                            settingsViewModel.dispatch(SettingsAction.Logout)
                        },
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
