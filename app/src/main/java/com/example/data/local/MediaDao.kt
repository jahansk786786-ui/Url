package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UploadedMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM uploaded_media ORDER BY uploadTimestamp DESC")
    fun getAllMedia(): Flow<List<UploadedMedia>>

    @Query("SELECT * FROM uploaded_media WHERE id = :id")
    suspend fun getMediaById(id: Long): UploadedMedia?

    @Query("SELECT * FROM uploaded_media WHERE slug = :slug LIMIT 1")
    suspend fun getMediaBySlug(slug: String): UploadedMedia?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: UploadedMedia): Long

    @Update
    suspend fun updateMedia(media: UploadedMedia)

    @Query("DELETE FROM uploaded_media WHERE id = :id")
    suspend fun deleteMediaById(id: Long)

    @Query("DELETE FROM uploaded_media WHERE id IN (:ids)")
    suspend fun deleteMediaBatch(ids: List<Long>)

    @Query("DELETE FROM uploaded_media WHERE expiryTimestamp IS NOT NULL AND expiryTimestamp < :currentTime")
    suspend fun deleteExpired(currentTime: Long): Int

    @Query("SELECT COUNT(*) FROM uploaded_media")
    fun getMediaCount(): Flow<Int>

    @Query("SELECT SUM(fileSizeBytes + bandwidthConsumedBytes) FROM uploaded_media")
    fun getTotalBandwidth(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM uploaded_media WHERE uploaderIp = :ip AND uploadTimestamp > :sinceTimestamp")
    suspend fun getUploadCountByIpSince(ip: String, sinceTimestamp: Long): Int
}
