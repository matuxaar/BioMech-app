package com.biomech.app

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication
import platform.UIKit.UIActivityViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberCsvShareLauncher(): (name: String, bytes: ByteArray) -> Unit {
    return { name, bytes ->
        try {
            val documentsDir = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                appropriateForURL = null,
                create = false,
                error = null,
            ) ?: return@try
            val fileURL = documentsDir.URLByAppendingPathComponent(name) ?: return@try
            val nsData = NSData.create(bytes = bytes, length = bytes.size.toULong())
            nsData.writeToURL(fileURL, atomically = true)

            val controller = UIActivityViewController(
                activityItems = listOf(fileURL),
                applicationActivities = null,
            )
            val root = UIApplication.sharedApplication.keyWindow?.rootViewController
            root?.presentViewController(controller, animated = true, completion = null)
        } catch (_: Exception) { }
    }
}
