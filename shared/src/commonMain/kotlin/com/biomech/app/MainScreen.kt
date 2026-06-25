package com.biomech.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.biomech.core.navigation.LocalNavigator
import com.biomech.core.navigation.Navigator
import com.biomech.domain.model.Device
import com.biomech.domain.repository.DeviceRepository
import com.biomech.domain.repository.AuthRepository
import com.biomech.domain.repository.TrainingRepository
import com.biomech.domain.usecase.LoginUseCase
import com.biomech.feature.home.HomeScreen
import com.biomech.feature.home.HomeViewModel
import com.biomech.feature.profile.ProfileScreen
import com.biomech.feature.profile.ProfileViewModel
import com.biomech.feature.settings.SettingsScreen
import com.biomech.feature.settings.SettingsViewModel
import com.biomech.feature.devices.AddDeviceBottomSheet
import com.biomech.feature.devices.DevicesViewModel
import org.koin.compose.koinInject

enum class BottomTab { HOME, PROFILE, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navigator = LocalNavigator.current
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showAddDeviceSheet by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("user@example.com") }

    val homeViewModel: HomeViewModel = koinInject()
    val homeState by homeViewModel.state.collectAsState()

    val profileViewModel: ProfileViewModel = koinInject()
    val profileState by profileViewModel.state.collectAsState()

    val settingsViewModel: SettingsViewModel = koinInject()
    val settingsState by settingsViewModel.state.collectAsState()

    val devicesViewModel: DevicesViewModel = koinInject()
    val devicesState by devicesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadDevices()
        profileViewModel.loadProfile()
        profileViewModel.setEmail(email)
    }

    if (showAddDeviceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddDeviceSheet = false }
        ) {
            AddDeviceBottomSheet(
                scannedDevices = devicesState.scannedDevices,
                isScanning = devicesState.isScanning,
                onStartScan = { devicesViewModel.startScan() },
                onStopScan = { devicesViewModel.stopScan() },
                onConnect = { deviceId ->
                    devicesViewModel.connect(deviceId)
                    showAddDeviceSheet = false
                },
                onDismiss = { showAddDeviceSheet = false },
            )
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
                    onClick = { selectedTab = BottomTab.HOME },
                    icon = { Text("🏠") },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.PROFILE,
                    onClick = { selectedTab = BottomTab.PROFILE },
                    icon = { Text("👤") },
                    label = { Text("Profile") },
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.SETTINGS,
                    onClick = { selectedTab = BottomTab.SETTINGS },
                    icon = { Text("⚙️") },
                    label = { Text("Settings") },
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            BottomTab.HOME -> {
                HomeScreen(
                    devices = homeState.devices,
                    onAddDevice = { showAddDeviceSheet = true },
                    onDeviceClick = { deviceId ->
                        navigator.navigateTo(com.biomech.core.navigation.Screen.Training)
                    },
                )
            }
            BottomTab.PROFILE -> {
                ProfileScreen(
                    email = profileState.email,
                    onLogout = {
                        profileViewModel.logout()
                        navigator.navigateAndClear(com.biomech.core.navigation.Screen.Login)
                    },
                )
            }
            BottomTab.SETTINGS -> {
                SettingsScreen(
                    serverUrl = settingsState.serverUrl,
                    onServerUrlChange = { settingsViewModel.updateServerUrl(it) },
                    onLogout = {
                        settingsViewModel.logout()
                        navigator.navigateAndClear(com.biomech.core.navigation.Screen.Login)
                    },
                )
            }
        }
    }
}
