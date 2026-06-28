package com.biomech.core.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Main : Screen("main")
    data class Dashboard(val deviceId: String = "") : Screen("dashboard")
    data object Training : Screen("training")
}
