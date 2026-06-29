package com.biomech.app

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memcpy
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeCommaSeparatedText
import platform.UniformTypeIdentifiers.UTTypePlainText
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFilePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit {
    return {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return@Unit
        val picker = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypePlainText, UTTypeCommaSeparatedText),
            asCopy = true,
        )
        picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
                val nsData = NSData.dataWithContentsOfURL(url) ?: return
                val size = nsData.length.toInt()
                val bytes = if (size > 0) {
                    ByteArray(size).apply {
                        usePinned { pinned ->
                            memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                        }
                    }
                } else {
                    ByteArray(0)
                }
                val fileName = url.lastPathComponent ?: "file.csv"
                onResult(bytes, fileName)
            }
        }
        root.presentViewController(picker, animated = true, completion = null)
    }
}
