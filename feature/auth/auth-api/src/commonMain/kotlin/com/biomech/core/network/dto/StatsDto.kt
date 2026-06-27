package com.biomech.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsDto(
    @SerialName("device_count") val deviceCount: Int = 0,
    @SerialName("total_trainings") val totalTrainings: Int = 0,
    @SerialName("completed_trainings") val completedTrainings: Int = 0,
    @SerialName("average_accuracy") val averageAccuracy: Double = 0.0,
    @SerialName("top_movements") val topMovements: List<String> = emptyList(),
)
