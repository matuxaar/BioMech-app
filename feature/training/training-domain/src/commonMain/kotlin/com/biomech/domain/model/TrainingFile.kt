package com.biomech.domain.model

data class TrainingFile(
    val id: String,
    val originalName: String,
    val fileSize: Long,
    val label: String,
    val createdAt: String,
)
