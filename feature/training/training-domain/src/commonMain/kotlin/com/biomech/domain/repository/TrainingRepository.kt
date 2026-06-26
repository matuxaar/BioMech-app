package com.biomech.domain.repository

import com.biomech.core.common.AppResult
import com.biomech.domain.model.TrainingFile
import com.biomech.domain.model.TrainingJob

interface TrainingRepository {
    suspend fun createJob(sessionIds: List<String>): AppResult<TrainingJob>
    suspend fun getJobs(): AppResult<List<TrainingJob>>
    suspend fun uploadFile(deviceId: String, label: String, fileBytes: ByteArray, fileName: String): AppResult<TrainingFile>
    suspend fun getFiles(): AppResult<List<TrainingFile>>
    suspend fun deleteFile(id: String): AppResult<Unit>
}
