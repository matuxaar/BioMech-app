package com.biomech.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.biomech.core.base.theme.BioMechTheme
import com.biomech.core.navigation.LocalNavigator
import com.biomech.core.navigation.Navigator
import com.biomech.core.navigation.Screen
import com.biomech.feature.auth.LoginScreen
import com.biomech.feature.auth.LoginViewModel
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
                                navigator.navigateAndClear(Screen.Main)
                            }
                        }

                        LoginScreen(
                            onLogin = { email, password -> viewModel.login(email, password) },
                            onRegister = { email, password -> viewModel.login(email, password) },
                        )
                    }

                    Screen.Main -> {
                        MainScreen()
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
                }
            }
        }
    }
}
