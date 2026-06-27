package com.biomech.domain.model

data class DashboardStats(
    val deviceCount: Int = 0,
    val totalTrainings: Int = 0,
    val completedTrainings: Int = 0,
    val averageAccuracy: Double = 0.0,
    val topMovements: List<String> = emptyList(),
)
