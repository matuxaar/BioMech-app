package com.biomech.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.biomech.core.database.dao.DeviceDao
import com.biomech.core.database.dao.EMGSessionDao
import com.biomech.core.database.dao.OfflineQueueDao
import com.biomech.core.database.dao.TrainingJobDao
import com.biomech.core.database.entity.CachedDevice
import com.biomech.core.database.entity.CachedEMGSession
import com.biomech.core.database.entity.CachedTrainingJob
import com.biomech.core.database.entity.OfflineQueueEntry

@Database(
    entities = [
        CachedDevice::class,
        CachedEMGSession::class,
        CachedTrainingJob::class,
        OfflineQueueEntry::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun emgSessionDao(): EMGSessionDao
    abstract fun trainingJobDao(): TrainingJobDao
    abstract fun offlineQueueDao(): OfflineQueueDao
}
