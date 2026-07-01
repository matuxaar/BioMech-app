package com.biomech.app

import androidx.compose.runtime.Composable
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Composable
actual fun rememberCsvDownloader(): (name: String, bytes: ByteArray) -> Unit {
    return { name, bytes ->
        val paths = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val docsDir = paths.first()
        val fileUrl = docsDir.URLByAppendingPathComponent(name)
        val nsData = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        nsData.writeToURL(fileUrl, atomically = true)
    }
}
