package com.biomech.core.database

import androidx.room.Room
import com.biomech.core.common.PlatformContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun createRoomDatabase(context: PlatformContext): AppDatabase {
    val documentsDir = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val path = documentsDir?.path ?: "./"
    return Room.databaseBuilder(
        "$path/biomech_cache.db",
        AppDatabase::class.java,
        "biomech_cache.db",
    ).build()
}
