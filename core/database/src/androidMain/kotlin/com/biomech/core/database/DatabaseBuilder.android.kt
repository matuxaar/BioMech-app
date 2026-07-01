package com.biomech.core.database

import androidx.room.migration.Migration
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.biomech.core.common.PlatformContext

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN bleServiceUuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN bleCommandCharUuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN bleStatusCharUuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN bleEmgCharUuid TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN last_recording_at INTEGER")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN last_training_at INTEGER")
        db.execSQL("ALTER TABLE cached_devices ADD COLUMN updated_at INTEGER")
        db.execSQL("ALTER TABLE cached_emg_sessions ADD COLUMN updated_at INTEGER")
        db.execSQL("ALTER TABLE cached_training_jobs ADD COLUMN updated_at INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS offline_queue (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                method TEXT NOT NULL,
                path TEXT NOT NULL,
                bodyJson TEXT,
                createdAt INTEGER NOT NULL,
                retryCount INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE cached_training_jobs ADD COLUMN created_at TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE cached_training_jobs ADD COLUMN errorMessage TEXT NOT NULL DEFAULT ''")
    }
}

actual fun createRoomDatabase(context: PlatformContext): AppDatabase? {
    return Room.databaseBuilder(
        context.androidContext,
        AppDatabase::class.java,
        "biomech_cache.db",
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
}
