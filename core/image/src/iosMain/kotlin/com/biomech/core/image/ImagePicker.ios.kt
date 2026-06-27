package com.biomech.core.image

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit {
    return {}
}
