package com.biomech.app

import androidx.compose.runtime.Composable
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIWindowScene

@Composable
actual fun rememberCsvShareLauncher(): (name: String, bytes: ByteArray) -> Unit {
    return { name, bytes ->
        val paths = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        val docsDir = paths.first()
        val fileUrl = docsDir.URLByAppendingPathComponent("share_$name")
        val nsData = bytes.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = bytes.size.toULong())
        }
        nsData.writeToURL(fileUrl, atomically = true)

        val controller = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )
        val root = UIApplication.sharedApplication.connectedScenes
            .firstOrNull { it is UIWindowScene }
            ?.let { it as UIWindowScene }
            ?.windows?.firstOrNull()
            ?.rootViewController
            ?: UIApplication.sharedApplication.keyWindow?.rootViewController
        root?.presentViewController(controller, animated = true, completion = null)
    }
}
