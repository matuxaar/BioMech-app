package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.OfflineQueueManager
import com.biomech.core.network.api.TrainingApi
import com.biomech.core.network.dto.CreateTrainingJobRequest
import com.biomech.core.network.networkJson
import com.biomech.domain.model.TrainingFile
import com.biomech.domain.model.TrainingJob
import com.biomech.domain.model.TrainingStatus
import com.biomech.domain.repository.TrainingRepository
import kotlinx.serialization.encodeToString

open class TrainingRepositoryImpl(
    protected val trainingApi: TrainingApi,
    protected val offlineQueueManager: OfflineQueueManager? = null,
) : TrainingRepository {

    override suspend fun createJob(sessionIds: List<String>): AppResult<TrainingJob> {
        return try {
            val dto = trainingApi.createJob(CreateTrainingJobRequest(sessionIds))
            val job = dto.toDomain()
            afterJobCreated(job)
            AppResult.Success(job)
        } catch (e: Exception) {
            val body = networkJson.encodeToString(CreateTrainingJobRequest(sessionIds))
            offlineQueueManager?.enqueueIfOffline("POST", "/api/v1/training/jobs", body)
            AppResult.Error(e.message ?: "Failed to create training job")
        }
    }

    override suspend fun getJobs(): AppResult<List<TrainingJob>> {
        return try {
            val response = trainingApi.getJobs()
            val jobs = response.data.map { it.toDomain() }
            afterJobsFetched(jobs)
            AppResult.Success(jobs)
        } catch (e: Exception) {
            getCachedJobs() ?: AppResult.Error(e.message ?: "Failed to fetch training jobs")
        }
    }

    override suspend fun uploadFile(deviceId: String, label: String, fileBytes: ByteArray, fileName: String): AppResult<TrainingFile> {
        return try {
            val dto = trainingApi.uploadFile(deviceId, label, fileBytes, fileName)
            AppResult.Success(dto.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to upload file")
        }
    }

    override suspend fun getFiles(): AppResult<List<TrainingFile>> {
        return try {
            val response = trainingApi.getFiles()
            AppResult.Success(response.data.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to fetch files")
        }
    }

    override suspend fun deleteFile(id: String): AppResult<Unit> {
        return try {
            trainingApi.deleteFile(id)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            offlineQueueManager?.enqueueIfOffline("DELETE", "/api/v1/training/files/$id")
            AppResult.Error(e.message ?: "Failed to delete file")
        }
    }

    protected open suspend fun afterJobCreated(job: TrainingJob) {}
    protected open suspend fun afterJobsFetched(jobs: List<TrainingJob>) {}
    protected open suspend fun getCachedJobs(): AppResult<List<TrainingJob>>? = null
}

internal fun com.biomech.core.network.dto.TrainingJobDto.toDomain() = TrainingJob(
    id = id,
    sessionIds = session_ids,
    status = when (status) {
        "pending" -> TrainingStatus.PENDING
        "running" -> TrainingStatus.RUNNING
        "completed" -> TrainingStatus.COMPLETED
        "failed" -> TrainingStatus.FAILED
        else -> TrainingStatus.PENDING
    },
    accuracy = accuracy,
    createdAt = created_at,
    updatedAt = updated_at,
    errorMessage = error_message,
)

internal fun com.biomech.core.network.dto.TrainingFileDto.toDomain() = TrainingFile(
    id = id,
    originalName = original_name,
    fileSize = file_size,
    label = label,
    createdAt = created_at,
)
