package com.biomech.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import com.biomech.core.common.PlatformContext
import com.biomech.core.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initKoin(PlatformContext(applicationContext))
        setContent {
            SideEffect {
                val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val isDark = nightMode == Configuration.UI_MODE_NIGHT_YES
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightNavigationBars = !isDark
            }
            App()
        }
    }
}
