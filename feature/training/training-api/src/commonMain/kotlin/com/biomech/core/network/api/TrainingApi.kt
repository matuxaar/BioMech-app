package com.biomech.core.network.api

import com.biomech.core.network.checkError
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TrainingApi {
    private val client = createHttpClient()

    suspend fun createJob(request: CreateTrainingJobRequest): TrainingJobDto {
        return client.post("/api/v1/training/jobs") {
            setBody(request)
        }.checkError().body()
    }

    suspend fun getJobs(): List<TrainingJobDto> {
        return client.get("/api/v1/training/jobs").checkError().body()
    }
}
