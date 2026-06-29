package com.biomech.app

import androidx.compose.runtime.Composable
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Composable
actual fun rememberCsvDownloader(): (name: String, bytes: ByteArray) -> Unit {
    return { name, bytes ->
        try {
            val documentsDir = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            val fileURL = documentsDir?.URLByAppendingPathComponent(name)
            fileURL?.let { url ->
                val data = platform.Foundation.NSData.create(bytes = bytes, length = bytes.size.toULong())
                data.writeToURL(url, atomically = true)
            }
        } catch (_: Exception) { }
    }
}
