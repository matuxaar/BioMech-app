package com.biomech.core.crash

import com.biomech.core.common.PlatformContext
import com.biomech.core.logger.Logger
import platform.Foundation.NSSetUncaughtExceptionHandler

actual class CrashReporter {
    private val tags = mutableMapOf<String, String>()

    actual fun init() {
        NSSetUncaughtExceptionHandler { _ ->
            Logger.e("CrashReporter", "Uncaught exception")
        }
    }

    actual fun recordException(throwable: Throwable) {
        Logger.e("CrashReporter", "Recorded exception", throwable)
    }

    actual fun setUserId(userId: String) {
        Logger.d("CrashReporter", "User: $userId")
    }

    actual fun addTag(key: String, value: String) {
        tags[key] = value
    }
}

actual fun createCrashReporter(context: PlatformContext): CrashReporter =
    CrashReporter()
