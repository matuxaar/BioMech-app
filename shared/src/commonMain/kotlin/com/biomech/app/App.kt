package com.biomech.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.biomech.core.base.theme.BioMechTheme
import com.biomech.core.common.ThemeMode
import com.biomech.core.navigation.LocalNavigator
import com.biomech.core.navigation.Navigator
import com.biomech.core.navigation.Screen
import com.biomech.core.navigation.SystemBackHandler
import com.biomech.core.network.ApiConfig
import com.biomech.core.resource.AppResources
import com.biomech.core.resource.Locale
import com.biomech.core.storage.KeyValueStorage
import com.biomech.domain.repository.AuthRepository
import com.biomech.feature.auth.LoginAction
import com.biomech.feature.auth.LoginEvent
import com.biomech.feature.auth.LoginScreen
import com.biomech.feature.auth.LoginViewModel
import com.biomech.feature.dashboard.DashboardAction
import com.biomech.feature.dashboard.DashboardScreen
import com.biomech.feature.dashboard.DashboardViewModel
import com.biomech.feature.training.TrainingAction
import com.biomech.feature.training.TrainingScreen
import com.biomech.feature.training.TrainingViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.coroutines.withTimeout
import org.koin.compose.koinInject

private const val KEY_THEME_MODE = "theme_mode"

private val healthClient by lazy {
    HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 3_000
            connectTimeoutMillis = 3_000
        }
    }
}

private suspend fun isApiAvailable(): Boolean {
    return try {
        withTimeout(4_000) {
            val response = healthClient.get(ApiConfig.healthUrl)
            response.status == io.ktor.http.HttpStatusCode.OK
        }
    } catch (_: Exception) {
        false
    }
}

@Composable
fun App() {
    val navigator = remember { Navigator() }
    val authRepo: AuthRepository = koinInject()
    val storage: KeyValueStorage = koinInject()
    var startupState by remember { mutableStateOf<AppStartupState>(AppStartupState.Loading) }
    var restartTrigger by remember { mutableStateOf(0) }
    var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var useGridLayout by remember { mutableStateOf(true) }
    var locale by remember { mutableStateOf(Locale.EN) }

    LaunchedEffect(Unit) {
        themeMode = ThemeMode.fromValue(storage.getInt(KEY_THEME_MODE))
        useGridLayout = storage.getBoolean("grid_layout", true)
        locale = Locale.fromValue(storage.getInt("locale", 0))
        AppResources.setLocale(locale)
        val savedUrl = storage.getString("server_url")
        if (savedUrl.isNotBlank()) {
            ApiConfig.baseUrl = savedUrl
        }
    }

    SystemBackHandler(
        enabled = navigator.currentScreen != Screen.Login,
        onBack = { navigator.goBack() },
    )

    LaunchedEffect(restartTrigger) {
        startupState = AppStartupState.Loading
        val apiUp = isApiAvailable()
        if (!apiUp) {
            startupState = AppStartupState.Offline
            navigator.navigateAndClear(Screen.Main)
            return@LaunchedEffect
        }
        val savedToken = authRepo.getToken()
        if (savedToken != null) {
            ApiConfig.token = savedToken
            startupState = AppStartupState.Ready
            navigator.navigateAndClear(Screen.Main)
        } else {
            startupState = AppStartupState.Ready
            navigator.navigateAndClear(Screen.Login)
        }
    }

    if (startupState == AppStartupState.Loading) return

    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    BioMechTheme(darkTheme = isDark) {
        CompositionLocalProvider(LocalNavigator provides navigator) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when (navigator.currentScreen) {
                    Screen.Login -> {
                        val viewModel: LoginViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.event.collect { event ->
                                when (event) {
                                    LoginEvent.NavigateToMain -> navigator.navigateAndClear(Screen.Main)
                                }
                            }
                        }

                        LoginScreen(
                            isLoading = state.isLoading,
                            error = state.error,
                            onLogin = { email, password ->
                                viewModel.dispatch(LoginAction.Login(email, password))
                            },
                            onRegister = { email, password ->
                                viewModel.dispatch(LoginAction.Register(email, password))
                            },
                            onSkip = {
                                navigator.navigateAndClear(Screen.Main)
                            },
                        )
                    }

                    Screen.Main -> {
                        MainScreen(
                            isOffline = startupState == AppStartupState.Offline,
                            onConnectionRestored = {
                                restartTrigger++
                            },
                            themeMode = themeMode,
                            onThemeModeChanged = { mode: ThemeMode ->
                                themeMode = mode
                                storage.putInt(KEY_THEME_MODE, mode.value)
                            },
                            useGridLayout = useGridLayout,
                            onGridLayoutChanged = { grid ->
                                useGridLayout = grid
                                storage.putBoolean("grid_layout", grid)
                            },
                            locale = locale,
                            onLocaleChanged = { l ->
                                locale = l
                                AppResources.setLocale(l)
                                storage.putInt("locale", l.value)
                            },
                        )
                    }

                    is Screen.Dashboard -> {
                        val viewModel: DashboardViewModel = koinInject()
                        val state by viewModel.state.collectAsState()

                        LaunchedEffect(Unit) {
                            val deviceId = (navigator.currentScreen as? Screen.Dashboard)?.deviceId
                            if (!deviceId.isNullOrBlank()) {
                                viewModel.dispatch(DashboardAction.SelectDevice(deviceId))
                            }
                        }

                        DashboardScreen(
                            deviceConnected = state.deviceConnected,
                            emgData = state.emgData,
                            predictionLabel = state.predictionLabel,
                            streamConnected = state.streamConnected,
                            isRecording = state.isRecording,
                            deviceActions = state.deviceActions,
                            onStartRecording = { viewModel.dispatch(DashboardAction.StartRecording) },
                            onStopRecording = { viewModel.dispatch(DashboardAction.StopRecording) },
                            onNavigateToTraining = { navigator.navigateTo(Screen.Training) },
                            onSendActionCode = { viewModel.dispatch(DashboardAction.SendActionCode(it)) },
                            onBack = { navigator.goBack() },
                        )
                    }

                    Screen.Training -> {
                        val viewModel: TrainingViewModel = koinInject()
                        val state by viewModel.state.collectAsState()
                        val pickFile = rememberFilePickerLauncher { bytes, fileName ->
                            viewModel.dispatch(TrainingAction.UploadFile(
                                state.selectedDeviceId ?: "", state.uploadLabel, bytes, fileName
                            ))
                        }

                        TrainingScreen(
                            jobs = state.jobs,
                            sessions = state.sessions,
                            selectedSessionIds = state.selectedSessionIds,
                            isCreating = state.isCreating,
                            error = state.error,
                            files = state.files,
                            isUploading = state.isUploading,
                            uploadError = state.uploadError,
                            selectedTab = state.selectedTab,
                            devices = state.devices,
                            selectedDeviceId = state.selectedDeviceId,
                            uploadLabel = state.uploadLabel,
                            onBack = { navigator.goBack() },
                            onToggleSession = { viewModel.dispatch(TrainingAction.ToggleSession(it)) },
                            onStartTraining = { viewModel.dispatch(TrainingAction.StartTraining) },
                            onSelectTab = { viewModel.dispatch(TrainingAction.SelectTab(it)) },
                            onDeleteFile = { viewModel.dispatch(TrainingAction.DeleteFile(it)) },
                            onUploadClick = { pickFile() },
                            onSelectDevice = { viewModel.dispatch(TrainingAction.SelectDevice(it)) },
                            onUploadLabelChange = { viewModel.dispatch(TrainingAction.UpdateUploadLabel(it)) },
                        )
                    }
                }
            }
        }
    }
}

private sealed interface AppStartupState {
    data object Loading : AppStartupState
    data object Ready : AppStartupState
    data object Offline : AppStartupState
}
