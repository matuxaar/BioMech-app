package com.biomech.domain.model

data class TrainingJob(
    val id: String,
    val sessionIds: List<String>,
    val status: TrainingStatus,
    val accuracy: Double,
    val createdAt: String = "",
    val updatedAt: String = "",
    val errorMessage: String = "",
)

enum class TrainingStatus {
    PENDING, RUNNING, COMPLETED, FAILED
}
