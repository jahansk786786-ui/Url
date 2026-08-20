package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "uploaded_media")
data class UploadedMedia(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val slug: String,
    val directUrl: String,
    val thumbnailUrl: String,
    val localUri: String? = null,
    val fileSizeBytes: Long,
    val fileExtension: String,
    val dimensions: String = "1920x1080",
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val expiryOption: String = "NEVER", // "10_MINS", "24_HOURS", "7_DAYS", "NEVER"
    val expiryTimestamp: Long? = null,
    val compressionLevel: String = "ORIGINAL", // "ORIGINAL", "HIGH_80", "BALANCED_60", "COMPACT_40"
    val storageProvider: String = "ImageKit", // "ImageKit", "Cloudinary", "Firebase Storage", "AWS S3"
    val uploaderIp: String = "192.168.1.105",
    val isNsfwModerated: Boolean = false,
    val nsfwScore: Float = 0.02f,
    val viewsCount: Int = 0,
    val bandwidthConsumedBytes: Long = 0L
) {
    val isExpired: Boolean
        get() = expiryTimestamp != null && System.currentTimeMillis() > expiryTimestamp

    val htmlEmbedCode: String
        get() = """<img src="$directUrl" alt="$title" loading="lazy" />"""

    val markdownCode: String
        get() = """![$title]($directUrl)"""

    val pageUrl: String
        get() = "https://cloudpix.io/i/$slug"
}
