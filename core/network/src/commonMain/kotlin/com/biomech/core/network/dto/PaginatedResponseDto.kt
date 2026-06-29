package com.biomech.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponseDto<T>(
    val data: List<T>,
    val total: Long,
    val page: Int,
    val limit: Int,
    val total_pages: Int,
)
