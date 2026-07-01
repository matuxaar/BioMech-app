package com.biomech.core.network

import com.biomech.core.common.currentTimeMillis
import com.biomech.core.connectivity.ConnectionStatus
import com.biomech.core.connectivity.ConnectivityObserver
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OfflineQueueEntry(
    val id: Long = 0,
    val method: String,
    val path: String,
    val bodyJson: String? = null,
    val createdAt: Long = currentTimeMillis(),
)

interface QueueStorage {
    suspend fun insert(entry: OfflineQueueEntry)
    suspend fun getAll(): List<OfflineQueueEntry>
    suspend fun delete(entry: OfflineQueueEntry)
}

class OfflineQueueManager(
    private val storage: QueueStorage,
    private val connectivityObserver: ConnectivityObserver,
    private val httpClient: HttpClient,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var flushJob: Job? = null

    fun start() {
        flushJob = scope.launch {
            connectivityObserver.status
                .collect { status ->
                    if (status == ConnectionStatus.AVAILABLE) {
                        flush()
                    }
                }
        }
    }

    fun stop() {
        flushJob?.cancel()
        scope.cancel()
    }

    suspend fun enqueue(method: String, path: String, bodyJson: String? = null) {
        storage.insert(
            OfflineQueueEntry(
                method = method,
                path = path,
                bodyJson = bodyJson,
                createdAt = currentTimeMillis(),
            )
        )
    }

    suspend fun enqueueIfOffline(method: String, path: String, bodyJson: String? = null): Boolean {
        if (connectivityObserver.status.value != ConnectionStatus.AVAILABLE) {
            enqueue(method, path, bodyJson)
            return true
        }
        return false
    }

    suspend fun flush() {
        val entries = storage.getAll()
        for (entry in entries) {
            try {
                replay(entry)
                storage.delete(entry)
            } catch (_: Exception) {
                // will retry on next connectivity change
            }
        }
    }

    private suspend fun replay(entry: OfflineQueueEntry) {
        val response: HttpResponse = when (entry.method.uppercase()) {
            "GET" -> httpClient.get(entry.path)
            "POST" -> httpClient.post(entry.path) {
                entry.bodyJson?.let { setBody(it) }
            }
            "PUT" -> httpClient.put(entry.path) {
                entry.bodyJson?.let { setBody(it) }
            }
            "DELETE" -> httpClient.delete(entry.path)
            else -> return
        }
        response.checkError()
    }
}
