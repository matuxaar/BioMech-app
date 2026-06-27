package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFilePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit {
    return {}
}
