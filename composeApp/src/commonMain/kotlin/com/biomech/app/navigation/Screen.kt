package com.biomech.app.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Devices : Screen("devices")
    data object Training : Screen("training")
    data object Settings : Screen("settings")
}
