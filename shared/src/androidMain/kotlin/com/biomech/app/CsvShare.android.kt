package com.biomech.app

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberCsvShareLauncher(): (name: String, bytes: ByteArray) -> Unit {
    val context = LocalContext.current
    return { name, bytes ->
        try {
            val dir = File(context.cacheDir, "shared_csvs")
            dir.mkdirs()
            val file = File(dir, name)
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share CSV"))
        } catch (_: Exception) { }
    }
}
