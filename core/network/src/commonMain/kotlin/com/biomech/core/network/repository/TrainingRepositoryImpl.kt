package com.biomech.core.network.repository

import com.biomech.core.common.AppResult
import com.biomech.core.network.api.TrainingApi
import com.biomech.core.network.dto.CreateTrainingJobRequest
import com.biomech.domain.model.TrainingJob
import com.biomech.domain.model.TrainingStatus
import com.biomech.domain.repository.TrainingRepository

class TrainingRepositoryImpl(
    private val trainingApi: TrainingApi,
) : TrainingRepository {

    override suspend fun createJob(sessionIds: List<String>): AppResult<TrainingJob> {
        return try {
            val dto = trainingApi.createJob(CreateTrainingJobRequest(sessionIds))
            AppResult.Success(dto.toDomain())
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to create training job")
        }
    }

    override suspend fun getJobs(): AppResult<List<TrainingJob>> {
        return try {
            val dtos = trainingApi.getJobs()
            AppResult.Success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Failed to fetch training jobs")
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
