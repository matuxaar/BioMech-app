package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.api.EMGApi
import com.biomech.core.network.dto.CreateSessionRequest
import com.biomech.domain.model.EMGSession
import com.biomech.domain.repository.EMGRepository

class EMGRepositoryImpl(
    private val emgApi: EMGApi,
) : EMGRepository {

    override suspend fun startSession(deviceId: String, label: String): AppResult<EMGSession> {
        return try {
            val dto = emgApi.startSession(CreateSessionRequest(deviceId, label))
            AppResult.Success(dto.toDomain())
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
            AppResult.Success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }
}

private fun com.biomech.core.network.dto.SessionDto.toDomain() = EMGSession(
    id = id,
    deviceId = device_id,
    label = label,
    startedAt = 0L, // TODO: parse ISO date
    endedAt = null,
)
