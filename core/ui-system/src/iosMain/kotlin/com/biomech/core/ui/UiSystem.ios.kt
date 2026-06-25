package com.biomech.core.ui

import com.biomech.core.common.PlatformContext
import platform.UIKit.UIApplication
import platform.UIKit.UIEdgeInsetsZero
import platform.UIKit.UIScreen
import platform.UIKit.UIResponder
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.NSNotificationCenter
import platform.Foundation.NSNotification
import platform.darwin.NSObject

actual class UiSystem {
    actual val safeAreaInsets: WindowInsets
        get() {
            val window = UIApplication.sharedApplication.keyWindow ?: return WindowInsets(0f, 0f, 0f, 0f)
            val insets = window.safeAreaInsets
            return WindowInsets(
                top = insets.top.toFloat(),
                bottom = insets.bottom.toFloat(),
                left = insets.left.toFloat(),
                right = insets.right.toFloat()
            )
        }

    actual fun hideKeyboard() {
        UIApplication.sharedApplication.keyWindow?.endEditing(true)
    }
}

actual fun createUiSystem(context: PlatformContext): UiSystem = UiSystem()
