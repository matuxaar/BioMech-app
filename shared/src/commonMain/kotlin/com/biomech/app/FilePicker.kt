package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit
