package com.biomech.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_emg_sessions")
data class CachedEMGSession(
    @PrimaryKey val id: String,
    val deviceId: String,
    val label: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
    val cachedAt: Long,
)
