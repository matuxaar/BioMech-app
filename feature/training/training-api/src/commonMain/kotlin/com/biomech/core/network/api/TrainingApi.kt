package com.biomech.core.network.api

import com.biomech.core.network.checkError
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class TrainingApi {
    private val client = createHttpClient()

    suspend fun createJob(request: CreateTrainingJobRequest): TrainingJobDto {
        return client.post("/api/v1/training/jobs") {
            setBody(request)
        }.checkError().body()
    }

    suspend fun getJobs(): PaginatedResponseDto<TrainingJobDto> {
        return client.get("/api/v1/training/jobs").checkError().body()
    }

    suspend fun uploadFile(deviceId: String, label: String, fileBytes: ByteArray, fileName: String): TrainingFileDto {
        return client.post("/api/v1/training/files") {
            setBody(MultiPartFormDataContent(formData {
                append("device_id", deviceId)
                append("label", label)
                append("file", fileBytes, Headers.build {
                    append(HttpHeaders.ContentType, "text/csv")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }))
        }.checkError().body()
    }

    suspend fun getFiles(): PaginatedResponseDto<TrainingFileDto> {
        return client.get("/api/v1/training/files").checkError().body()
    }

    suspend fun deleteFile(id: String) {
        client.delete("/api/v1/training/files/$id").checkError()
    }
}
