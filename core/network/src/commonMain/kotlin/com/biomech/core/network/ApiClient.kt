package com.biomech.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val networkJson = Json { ignoreUnknownKeys = true; isLenient = true }

object ApiConfig {
    var baseUrl: String = "http://localhost:8080/api/v1/"
    var token: String? = null
    var onTokenExpired: (suspend () -> Unit)? = null

    val apiPrefix: String
        get() {
            val clean = baseUrl.trimEnd('/')
            val idx = clean.indexOf("/api")
            return if (idx > 0) clean.substring(idx) else "/api/v1"
        }

    val healthUrl: String
        get() {
            val clean = baseUrl.trimEnd('/')
            val idx = clean.indexOf("/api")
            return if (idx > 0) clean.substring(0, idx) + "/health" else "$clean/health"
        }
}

private var sharedClient: HttpClient? = null

fun createHttpClient(): HttpClient {
    if (sharedClient == null) {
        sharedClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
            }

            defaultRequest {
                val p = URLBuilder().apply { takeFrom(ApiConfig.baseUrl) }
                url {
                    protocol = p.protocol
                    host = p.host
                    port = p.port
                }
                contentType(ContentType.Application.Json)
                ApiConfig.token?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                }
            }
        }
    }
    return sharedClient!!
}

fun resetHttpClient() {
    sharedClient?.close()
    sharedClient = null
}

suspend inline fun HttpResponse.checkError(): HttpResponse {
    if (!status.isSuccess()) {
        val body = try { bodyAsText() } catch (_: Exception) { null }
        val url = call.request.url.toString()
        if (status == HttpStatusCode.Unauthorized) {
            ApiConfig.onTokenExpired?.invoke()
        }
        throw Exception("[$url] ${status.value}: ${body ?: "unknown error"}")
    }
    return this
}
