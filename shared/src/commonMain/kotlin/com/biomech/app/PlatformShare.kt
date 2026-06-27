package com.biomech.app

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCsvShareLauncher(): (csvName: String, csvBytes: ByteArray) -> Unit
