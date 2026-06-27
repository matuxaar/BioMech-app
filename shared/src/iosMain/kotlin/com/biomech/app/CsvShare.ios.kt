package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCsvShareLauncher(): (name: String, bytes: ByteArray) -> Unit {
    return { _, _ -> }
}
