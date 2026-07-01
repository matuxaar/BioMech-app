package com.biomech.app

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberCsvDownloader(): (name: String, bytes: ByteArray) -> Unit {
    val context = LocalContext.current
    return { name, bytes ->
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(dir, name)
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
    }
}
