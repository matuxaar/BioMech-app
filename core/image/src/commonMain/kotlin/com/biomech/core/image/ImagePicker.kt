package com.biomech.core.image

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit
