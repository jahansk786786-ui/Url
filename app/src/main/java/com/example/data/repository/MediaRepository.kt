package com.example.data.repository

import com.example.data.local.MediaDao
import com.example.data.local.SettingsDao
import com.example.data.model.AdminSettings
import com.example.data.model.UploadedMedia
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class MediaRepository(
    private val mediaDao: MediaDao,
    private val settingsDao: SettingsDao
) {
    val allMedia: Flow<List<UploadedMedia>> = mediaDao.getAllMedia()
    val mediaCount: Flow<Int> = mediaDao.getMediaCount()
    val totalBandwidth: Flow<Long?> = mediaDao.getTotalBandwidth()
    val adminSettings: Flow<AdminSettings?> = settingsDao.getSettings()

    suspend fun getSettingsSync(): AdminSettings {
        return settingsDao.getSettingsSync() ?: AdminSettings().also {
            settingsDao.insertSettings(it)
        }
    }

    suspend fun updateSettings(settings: AdminSettings) {
        settingsDao.updateSettings(settings)
    }

    suspend fun deleteMediaById(id: Long) {
        mediaDao.deleteMediaById(id)
    }

    suspend fun deleteMediaBatch(ids: List<Long>) {
        mediaDao.deleteMediaBatch(ids)
    }

    suspend fun purgeExpiredMedia(): Int {
        return mediaDao.deleteExpired(System.currentTimeMillis())
    }

    suspend fun processAndUploadMedia(
        title: String,
        customSlug: String,
        sourceUri: String?,
        sourceUrl: String?,
        originalSizeBytes: Long,
        extension: String,
        expiryOption: String,
        compressionLevel: String,
        userIp: String = "192.168.1.105"
    ): Result<UploadedMedia> {
        val settings = getSettingsSync()

        // 1. Extension validation
        val cleanExt = extension.removePrefix(".").lowercase()
        val isAllowed = when (cleanExt) {
            "png" -> settings.allowedPng
            "jpg", "jpeg" -> settings.allowedJpg
            "webp" -> settings.allowedWebp
            "gif" -> settings.allowedGif
            "svg" -> settings.allowedSvg
            else -> false
        }
        if (!isAllowed) {
            return Result.failure(IllegalArgumentException("File extension '.$cleanExt' is not allowed by admin policy."))
        }

        // 2. Max File Size Cap validation
        if (originalSizeBytes > settings.maxFileSizeBytes) {
            val maxMb = settings.maxFileSizeBytes / (1024 * 1024)
            return Result.failure(IllegalArgumentException("File exceeds max size limit of $maxMb MB."))
        }

        // 3. Rate Limit check
        val oneHourAgo = System.currentTimeMillis() - 3600000L
        val recentUploads = mediaDao.getUploadCountByIpSince(userIp, oneHourAgo)
        if (recentUploads >= settings.ipRateLimitPerHour) {
            return Result.failure(IllegalStateException("Rate limit exceeded ($recentUploads/${settings.ipRateLimitPerHour} uploads/hour). Please wait."))
        }

        // 4. Calculate Compressed Size
        val compressionRatio = when (compressionLevel) {
            "HIGH_80" -> 0.80f
            "BALANCED_60" -> 0.60f
            "COMPACT_40" -> 0.40f
            else -> 1.0f
        }
        val finalSizeBytes = (originalSizeBytes * compressionRatio).toLong().coerceAtLeast(5000L)

        // 5. Expiry calculation
        val now = System.currentTimeMillis()
        val expiryTimestamp = when (expiryOption) {
            "10_MINS" -> now + (10 * 60 * 1000L)
            "24_HOURS" -> now + (24 * 3600 * 1000L)
            "7_DAYS" -> now + (7 * 24 * 3600 * 1000L)
            else -> null
        }

        // 6. Slug generation
        val finalSlug = if (customSlug.isNotBlank()) {
            customSlug.trim().replace("\\s+".toRegex(), "-").lowercase()
        } else {
            val randomHash = UUID.randomUUID().toString().substring(0, 8)
            val titleSlug = title.trim().replace("[^a-zA-Z0-9]".toRegex(), "-").take(16).lowercase()
            if (titleSlug.isNotBlank()) "$titleSlug-$randomHash" else randomHash
        }

        // 7. Active storage provider URL synthesis
        val activeProvider = settings.activeProvider
        val directUrl = if (!sourceUrl.isNullOrBlank()) {
            sourceUrl
        } else {
            when (activeProvider) {
                "ImageKit" -> "${settings.imageKitUrlEndpoint.trimEnd('/')}/$finalSlug.$cleanExt"
                "Cloudinary" -> "https://res.cloudinary.com/${settings.cloudinaryCloudName}/image/upload/v${now / 1000}/$finalSlug.$cleanExt"
                "Firebase Storage" -> "https://firebasestorage.googleapis.com/v0/b/${settings.firebaseBucket}/o/$finalSlug.$cleanExt?alt=media"
                "AWS S3" -> "https://${settings.awsS3Bucket}.s3.${settings.awsS3Region}.amazonaws.com/$finalSlug.$cleanExt"
                else -> "https://cdn.cloudpix.io/i/$finalSlug.$cleanExt"
            }
        }

        val thumbnailUrl = sourceUri ?: directUrl

        // 8. NSFW Moderation simulation / heuristic
        val simulatedNsfwScore = ((title.hashCode() and 0x7FFFFFFF) % 30) / 100f
        val isFlagged = settings.autoNsfwModeration && simulatedNsfwScore > settings.nsfwThreshold
        if (isFlagged && settings.blockOnNsfw) {
            return Result.failure(SecurityException("Upload blocked by automated NSFW moderation filter."))
        }

        val media = UploadedMedia(
            title = title.ifBlank { "Uploaded Image" },
            slug = finalSlug,
            directUrl = directUrl,
            thumbnailUrl = thumbnailUrl,
            localUri = sourceUri,
            fileSizeBytes = finalSizeBytes,
            fileExtension = cleanExt,
            dimensions = "1920x1080",
            uploadTimestamp = now,
            expiryOption = expiryOption,
            expiryTimestamp = expiryTimestamp,
            compressionLevel = compressionLevel,
            storageProvider = activeProvider,
            uploaderIp = userIp,
            isNsfwModerated = isFlagged,
            nsfwScore = simulatedNsfwScore,
            viewsCount = 1,
            bandwidthConsumedBytes = finalSizeBytes
        )

        val insertedId = mediaDao.insertMedia(media)
        return Result.success(media.copy(id = insertedId))
    }
}
