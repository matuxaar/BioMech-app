package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.common.currentTimeMillis
import com.biomech.core.database.dao.EMGSessionDao
import com.biomech.core.database.entity.CachedEMGSession
import com.biomech.core.network.api.EMGApi
import com.biomech.core.network.dto.CreateSessionRequest
import com.biomech.domain.model.EMGSession
import com.biomech.domain.repository.EMGRepository

class EMGRepositoryImpl(
    private val emgApi: EMGApi,
    private val emgSessionDao: EMGSessionDao,
) : EMGRepository {

    override suspend fun startSession(deviceId: String, label: String): AppResult<EMGSession> {
        return try {
            val dto = emgApi.startSession(CreateSessionRequest(deviceId, label))
            val session = dto.toDomain()
            emgSessionDao.insert(session.toCached())
            AppResult.Success(session)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to start session")
        }
    }

    override suspend fun endSession(sessionId: String): AppResult<Unit> {
        return try {
            emgApi.endSession(sessionId)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to end session")
        }
    }

    override suspend fun getSessions(): AppResult<List<EMGSession>> {
        return try {
            val dtos = emgApi.getSessions()
            val sessions = dtos.map { it.toDomain() }
            val cached = sessions.map { it.toCached() }
            emgSessionDao.clearAll()
            emgSessionDao.insertAll(cached)
            AppResult.Success(sessions)
        } catch (e: Exception) {
            val cached = emgSessionDao.getAll()
            if (cached.isNotEmpty()) {
                AppResult.Success(cached.map { it.toDomain() })
            } else {
                AppResult.Error(e.message ?: "Failed to fetch sessions")
            }
        }
    }
}

private fun com.biomech.core.network.dto.SessionDto.toDomain() = EMGSession(
    id = id,
    deviceId = device_id,
    label = label,
    startedAt = 0L,
    endedAt = null,
)

private fun EMGSession.toCached() = CachedEMGSession(
    id = id,
    deviceId = deviceId,
    label = label,
    startedAt = startedAt,
    endedAt = endedAt,
    cachedAt = currentTimeMillis(),
)

private fun CachedEMGSession.toDomain() = EMGSession(
    id = id,
    deviceId = deviceId,
    label = label,
    startedAt = startedAt,
    endedAt = endedAt,
)
