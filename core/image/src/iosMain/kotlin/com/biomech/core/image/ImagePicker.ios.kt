package com.biomech.core.image

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceTypePhotoLibrary
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit {
    return {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary
        picker.delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage ?: return
                val imageData = UIImageJPEGRepresentation(image, 0.8) ?: return
                val size = imageData.length.toInt()
                val bytes = if (size > 0) {
                    ByteArray(size).apply {
                        usePinned { pinned ->
                            memcpy(pinned.addressOf(0), imageData.bytes!!, imageData.length)
                        }
                    }
                } else {
                    ByteArray(0)
                }
                onResult(bytes, "photo.jpg")
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, completion = null)
            }
        }
        root.presentViewController(picker, animated = true, completion = null)
    }
}

private fun UIImageJPEGRepresentation(image: UIImage, compressionQuality: Double): NSData? =
    platform.UIKit.UIImageJPEGRepresentation(image, compressionQuality)
