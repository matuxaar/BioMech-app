package com.biomech.core.notification

import com.biomech.core.common.PlatformContext
import platform.UserNotifications.*

actual class NotificationManager {
    actual fun schedule(notification: NotificationData) {
        val content = UNMutableNotificationContent().apply {
            title = notification.title
            body = notification.body
        }
        val trigger = if (notification.delayMillis > 0) {
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                notification.delayMillis / 1000.0, repeats = false
            )
        } else null
        val request = UNNotificationRequest.requestWithIdentifier(
            notification.title, content, trigger
        )
        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { _ -> }
    }

    actual fun cancelAll() {
        UNUserNotificationCenter.currentNotificationCenter()
            .removeAllDeliveredNotifications()
    }
}

actual fun createNotificationManager(context: PlatformContext): NotificationManager =
    NotificationManager()
