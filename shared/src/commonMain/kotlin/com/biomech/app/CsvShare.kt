package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCsvShareLauncher(): (name: String, bytes: ByteArray) -> Unit
