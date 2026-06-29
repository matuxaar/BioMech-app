package com.biomech.domain.repository

import com.biomech.core.common.AppResult
import com.biomech.domain.model.EMGSession

import com.biomech.domain.model.EMGSample

interface EMGRepository {
    suspend fun startSession(deviceId: String, label: String): AppResult<EMGSession>
    suspend fun endSession(sessionId: String): AppResult<Unit>
    suspend fun getSessions(): AppResult<List<EMGSession>>
    suspend fun addSamplesBatch(sessionId: String, samples: List<EMGSample>): AppResult<Unit>
}
