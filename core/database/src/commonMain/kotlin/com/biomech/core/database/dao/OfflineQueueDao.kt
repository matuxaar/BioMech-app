package com.biomech.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.biomech.core.database.entity.OfflineQueueEntry

@Dao
interface OfflineQueueDao {

    @Query("SELECT * FROM offline_queue ORDER BY createdAt ASC")
    suspend fun getAll(): List<OfflineQueueEntry>

    @Insert
    suspend fun insert(entry: OfflineQueueEntry)

    @Query("DELETE FROM offline_queue WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM offline_queue")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM offline_queue")
    suspend fun count(): Int
}
