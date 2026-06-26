package com.biomech.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTrainingJobRequest(
    val session_ids: List<String>,
)

@Serializable
data class TrainingJobDto(
    val id: String,
    val user_id: String,
    val session_ids: List<String>,
    val status: String,
    val accuracy: Double,
    val created_at: String,
)

@Serializable
data class TrainingFileDto(
    val id: String,
    val original_name: String,
    val file_size: Long,
    val label: String = "",
    val created_at: String,
)
