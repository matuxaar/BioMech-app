package com.biomech.core.network.api

import com.biomech.core.network.checkError
import com.biomech.core.network.createHttpClient
import com.biomech.core.network.dto.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class EMGApi {
    private val client = createHttpClient()

    suspend fun startSession(request: CreateSessionRequest): SessionDto {
        return client.post("/api/v1/emg/sessions") {
            setBody(request)
        }.checkError().body()
    }

    suspend fun endSession(sessionId: String) {
        client.post("/api/v1/emg/sessions/$sessionId/end").checkError()
    }

    suspend fun getSessions(): List<SessionDto> {
        return client.get("/api/v1/emg/sessions").checkError().body()
    }

    suspend fun addSamplesBatch(sessionId: String, request: BatchSamplesRequest) {
        client.post("/api/v1/emg/sessions/$sessionId/samples/batch") {
            setBody(request)
        }.checkError()
    }

    suspend fun getSamples(sessionId: String): List<SampleDto> {
        return client.get("/api/v1/emg/sessions/$sessionId/samples").checkError().body()
    }
}
