package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCsvDownloader(): (name: String, bytes: ByteArray) -> Unit
