package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.common.currentTimeMillis
import com.biomech.core.database.dao.TrainingJobDao
import com.biomech.core.database.entity.CachedTrainingJob
import com.biomech.core.network.api.TrainingApi
import com.biomech.core.network.dto.CreateTrainingJobRequest
import com.biomech.domain.model.TrainingJob
import com.biomech.domain.model.TrainingStatus
import com.biomech.domain.repository.TrainingRepository

class TrainingRepositoryImpl(
    private val trainingApi: TrainingApi,
    private val trainingJobDao: TrainingJobDao,
) : TrainingRepository {

    override suspend fun createJob(sessionIds: List<String>): AppResult<TrainingJob> {
        return try {
            val dto = trainingApi.createJob(CreateTrainingJobRequest(sessionIds))
            val job = dto.toDomain()
            trainingJobDao.insert(job.toCached())
            AppResult.Success(job)
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to create training job")
        }
    }

    override suspend fun getJobs(): AppResult<List<TrainingJob>> {
        return try {
            val dtos = trainingApi.getJobs()
            val jobs = dtos.map { it.toDomain() }
            val cached = jobs.map { it.toCached() }
            trainingJobDao.clearAll()
            trainingJobDao.insertAll(cached)
            AppResult.Success(jobs)
        } catch (e: Exception) {
            val cached = trainingJobDao.getAll()
            if (cached.isNotEmpty()) {
                AppResult.Success(cached.map { it.toDomain() })
            } else {
                AppResult.Error(e.message ?: "Failed to fetch training jobs")
            }
        }
    }
}

private fun com.biomech.core.network.dto.TrainingJobDto.toDomain() = TrainingJob(
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
)

private fun TrainingJob.toCached() = CachedTrainingJob(
    id = id,
    sessionIds = sessionIds.joinToString(","),
    status = when (status) {
        TrainingStatus.PENDING -> "pending"
        TrainingStatus.RUNNING -> "running"
        TrainingStatus.COMPLETED -> "completed"
        TrainingStatus.FAILED -> "failed"
    },
    accuracy = accuracy,
    cachedAt = currentTimeMillis(),
)

private fun CachedTrainingJob.toDomain() = TrainingJob(
    id = id,
    sessionIds = sessionIds.split(",").filter { it.isNotBlank() },
    status = when (status) {
        "pending" -> TrainingStatus.PENDING
        "running" -> TrainingStatus.RUNNING
        "completed" -> TrainingStatus.COMPLETED
        "failed" -> TrainingStatus.FAILED
        else -> TrainingStatus.PENDING
    },
    accuracy = accuracy,
)
