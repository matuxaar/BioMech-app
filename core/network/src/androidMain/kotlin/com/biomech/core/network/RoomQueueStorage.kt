package com.biomech.core.network

import com.biomech.core.database.dao.OfflineQueueDao
import com.biomech.core.database.entity.OfflineQueueEntry as RoomEntry

class RoomQueueStorage(
    private val dao: OfflineQueueDao,
) : QueueStorage {

    override suspend fun insert(entry: OfflineQueueEntry) {
        dao.insert(
            RoomEntry(
                method = entry.method,
                path = entry.path,
                bodyJson = entry.bodyJson,
                createdAt = entry.createdAt,
            )
        )
    }

    override suspend fun getAll(): List<OfflineQueueEntry> {
        return dao.getAll().map { roomEntry ->
            OfflineQueueEntry(
                id = roomEntry.id,
                method = roomEntry.method,
                path = roomEntry.path,
                bodyJson = roomEntry.bodyJson,
                createdAt = roomEntry.createdAt,
            )
        }
    }

    override suspend fun delete(entry: OfflineQueueEntry) {
        dao.deleteById(entry.id)
    }
}
