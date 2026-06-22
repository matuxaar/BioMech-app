package com.biomech.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.biomech.app.navigation.LocalNavigator
import com.biomech.app.navigation.Navigator
import com.biomech.app.navigation.Screen
import com.biomech.core.ui.theme.BioMechTheme
import com.biomech.feature.auth.LoginScreen
import com.biomech.feature.auth.LoginViewModel
import com.biomech.feature.dashboard.DashboardScreen
import com.biomech.feature.dashboard.DashboardViewModel
import com.biomech.feature.devices.DevicesScreen
import com.biomech.feature.devices.DevicesViewModel
import com.biomech.feature.settings.SettingsScreen
import com.biomech.feature.settings.SettingsViewModel
import com.biomech.feature.training.TrainingScreen
import com.biomech.feature.training.TrainingViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val navigator = remember { Navigator() }

    BioMechTheme {
        CompositionLocalProvider(LocalNavigator provides navigator) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (navigator.currentScreen) {
                    Screen.Login -> {
                        val viewModel: LoginViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        LaunchedEffect(state.isLoggedIn) {
                            if (state.isLoggedIn) {
                                navigator.navigateAndClear(Screen.Dashboard)
                            }
                        }

                        LoginScreen(
                            onLogin = { email, password -> viewModel.login(email, password) },
                            onRegister = { email, password -> viewModel.login(email, password) },
                        )
                    }

                    Screen.Dashboard -> {
                        val viewModel: DashboardViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        DashboardScreen(
                            deviceConnected = state.deviceConnected,
                            emgData = state.emgData,
                            onStartRecording = { viewModel.startRecording() },
                            onStopRecording = { viewModel.stopRecording() },
                        )
                    }

                    Screen.Devices -> {
                        val viewModel: DevicesViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        DevicesScreen(
                            scannedDevices = state.scannedDevices,
                            isScanning = state.isScanning,
                            onStartScan = { viewModel.startScan() },
                            onStopScan = { viewModel.stopScan() },
                            onConnect = { viewModel.connect(it) },
                        )
                    }

                    Screen.Training -> {
                        val viewModel: TrainingViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        TrainingScreen(
                            jobs = state.jobs,
                            sessionLabels = state.sessionLabels,
                            selectedSessions = state.selectedSessions,
                            onToggleSession = { viewModel.toggleSession(it) },
                            onStartTraining = { viewModel.startTraining() },
                        )
                    }

                    Screen.Settings -> {
                        val viewModel: SettingsViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        SettingsScreen(
                            serverUrl = state.serverUrl,
                            onServerUrlChange = { viewModel.updateServerUrl(it) },
                            onLogout = {
                                viewModel.logout()
                                navigator.navigateAndClear(Screen.Login)
                            },
                        )
                    }
                }
            }
        }
    }
}
