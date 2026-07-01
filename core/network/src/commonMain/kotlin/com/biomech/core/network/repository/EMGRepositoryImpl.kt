package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.common.currentTimeMillis
import com.biomech.core.network.OfflineQueueManager
import com.biomech.core.network.api.EMGApi
import com.biomech.core.network.dto.BatchSamplesRequest
import com.biomech.core.network.dto.CreateSessionRequest
import com.biomech.core.network.dto.SampleRequest
import com.biomech.core.network.networkJson
import com.biomech.domain.model.EMGSample
import com.biomech.domain.model.EMGSession
import com.biomech.domain.repository.EMGRepository
import kotlinx.serialization.encodeToString

open class EMGRepositoryImpl(
    protected val emgApi: EMGApi,
    protected val offlineQueueManager: OfflineQueueManager? = null,
) : EMGRepository {

    override suspend fun startSession(deviceId: String, label: String): AppResult<EMGSession> {
        return try {
            val dto = emgApi.startSession(CreateSessionRequest(deviceId, label))
            val session = dto.toDomain()
            afterSessionCreated(session)
            AppResult.Success(session)
        } catch (e: Exception) {
            val body = networkJson.encodeToString(CreateSessionRequest(deviceId, label))
            offlineQueueManager?.enqueueIfOffline("POST", "/api/v1/emg/sessions", body)
            AppResult.Error(e.message ?: "Failed to start session")
        }
    }

    override suspend fun endSession(sessionId: String): AppResult<Unit> {
        return try {
            emgApi.endSession(sessionId)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            offlineQueueManager?.enqueueIfOffline("POST", "/api/v1/emg/sessions/$sessionId/end")
            AppResult.Error(e.message ?: "Failed to end session")
        }
    }

    override suspend fun addSamplesBatch(sessionId: String, samples: List<EMGSample>): AppResult<Unit> {
        return try {
            val request = BatchSamplesRequest(samples.map { it.toDto() })
            emgApi.addSamplesBatch(sessionId, request)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val body = networkJson.encodeToString(BatchSamplesRequest(samples.map { it.toDto() }))
            offlineQueueManager?.enqueueIfOffline("POST", "/api/v1/emg/sessions/$sessionId/samples/batch", body)
            AppResult.Error(e.message ?: "Failed to add samples")
        }
    }

    override suspend fun getSessions(): AppResult<List<EMGSession>> {
        return try {
            val response = emgApi.getSessions()
            val sessions = response.data.map { it.toDomain() }
            onSessionsFetched(sessions)
            AppResult.Success(sessions)
        } catch (e: Exception) {
            getCachedSessions() ?: AppResult.Error(e.message ?: "Failed to fetch sessions")
        }
    }

    protected open suspend fun afterSessionCreated(session: EMGSession) {}
    protected open suspend fun onSessionsFetched(sessions: List<EMGSession>) {}
    protected open suspend fun getCachedSessions(): AppResult<List<EMGSession>>? = null
}

internal fun com.biomech.core.network.dto.SessionDto.toDomain() = EMGSession(
    id = id,
    deviceId = device_id,
    label = label,
    startedAt = started_at.toLongOrNull() ?: 0L,
    endedAt = ended_at?.toLongOrNull(),
)

internal fun EMGSample.toDto() = SampleRequest(
    timestamp = currentTimeMillis().toString(),
    channel_1 = channel1,
    channel_2 = channel2,
    channel_3 = channel3,
    channel_4 = channel4,
    channel_5 = channel5,
    channel_6 = channel6,
    channel_7 = channel7,
    channel_8 = channel8,
)
