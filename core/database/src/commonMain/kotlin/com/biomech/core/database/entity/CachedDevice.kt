package com.biomech.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_devices")
data class CachedDevice(
    @PrimaryKey val id: String,
    val type: String,
    val name: String,
    val hwVersion: String,
    val bleServiceUuid: String = "",
    val bleCommandCharUuid: String = "",
    val bleStatusCharUuid: String = "",
    val bleEmgCharUuid: String = "",
    @ColumnInfo(name = "last_recording_at") val lastRecordingAt: Long? = null,
    @ColumnInfo(name = "last_training_at") val lastTrainingAt: Long? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
    val cachedAt: Long,
)
