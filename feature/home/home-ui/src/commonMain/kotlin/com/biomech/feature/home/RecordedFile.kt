package com.biomech.feature.home

data class RecordedFile(
    val fileName: String,
    val csvBytes: ByteArray,
    val deviceId: String,
    val deviceName: String,
    val label: String,
    val sampleCount: Int,
    val timestamp: Long,
) {
    val fileSize: Long get() = csvBytes.size.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RecordedFile
        return fileName == other.fileName && timestamp == other.timestamp
    }

    override fun hashCode(): Int = 31 * fileName.hashCode() + timestamp.hashCode()
}
