package com.biomech.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_queue")
data class OfflineQueueEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val method: String,
    val path: String,
    val bodyJson: String? = null,
    val createdAt: Long,
    val retryCount: Int = 0,
)
