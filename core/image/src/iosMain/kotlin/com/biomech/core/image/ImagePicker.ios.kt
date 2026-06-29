package com.biomech.core.image

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memcpy
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceTypePhotoLibrary
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onResult: (ByteArray, String) -> Unit): () -> Unit {
    return {
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return@Unit
        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary
        picker.delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
                picker.dismissViewControllerAnimated(true, completion = null)
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage ?: return
                val imageData = UIImageJPEGRepresentation(image, 0.8) ?: return
                val size = imageData.length.toInt()
                val bytes = if (size > 0) {
                    ByteArray(size).apply {
                        usePinned { pinned ->
                            memcpy(pinned.addressOf(0), imageData.bytes, imageData.length)
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

// UIImageJPEGRepresentation is a C function, needs to be linked via @ObjCName or similar
// In Kotlin/Native it's available as platform.UIKit.UIImageJPEGRepresentation
private fun UIImageJPEGRepresentation(image: UIImage, compressionQuality: Double): NSData? =
    platform.UIKit.UIImageJPEGRepresentation(image, compressionQuality)
