package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminSettings
import com.example.data.model.UploadedMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UploadedMedia::class, AdminSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cloudpix_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.mediaDao(), database.settingsDao())
                    }
                }
            }

            private suspend fun populateInitialData(mediaDao: MediaDao, settingsDao: SettingsDao) {
                settingsDao.insertSettings(AdminSettings())

                val now = System.currentTimeMillis()
                val sampleItems = listOf(
                    UploadedMedia(
                        title = "Cyberpunk Skyline 4K",
                        slug = "cyber-skyline-8x",
                        directUrl = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=1600&auto=format&fit=crop&q=80",
                        thumbnailUrl = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=400&auto=format&fit=crop&q=80",
                        fileSizeBytes = 3450000L,
                        fileExtension = "jpg",
                        dimensions = "3840x2160",
                        uploadTimestamp = now - 3600000L * 2,
                        expiryOption = "NEVER",
                        compressionLevel = "HIGH_80",
                        storageProvider = "ImageKit",
                        uploaderIp = "192.168.1.102",
                        isNsfwModerated = false,
                        nsfwScore = 0.01f,
                        viewsCount = 384,
                        bandwidthConsumedBytes = 1324800000L
                    ),
                    UploadedMedia(
                        title = "Emerald Alpine Lake",
                        slug = "emerald-alpine-lake",
                        directUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1600&auto=format&fit=crop&q=80",
                        thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&auto=format&fit=crop&q=80",
                        fileSizeBytes = 2190000L,
                        fileExtension = "png",
                        dimensions = "2560x1440",
                        uploadTimestamp = now - 3600000L * 14,
                        expiryOption = "7_DAYS",
                        expiryTimestamp = now + (7 * 24 * 3600000L),
                        compressionLevel = "BALANCED_60",
                        storageProvider = "Cloudinary",
                        uploaderIp = "10.0.0.45",
                        isNsfwModerated = false,
                        nsfwScore = 0.03f,
                        viewsCount = 812,
                        bandwidthConsumedBytes = 1778280000L
                    ),
                    UploadedMedia(
                        title = "Neon Minimalist Design Vector",
                        slug = "neon-vector-logo",
                        directUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1600&auto=format&fit=crop&q=80",
                        thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
                        fileSizeBytes = 890000L,
                        fileExtension = "webp",
                        dimensions = "1920x1080",
                        uploadTimestamp = now - 3600000L * 36,
                        expiryOption = "24_HOURS",
                        expiryTimestamp = now - 3600000L * 12, // Expired sample
                        compressionLevel = "COMPACT_40",
                        storageProvider = "Firebase Storage",
                        uploaderIp = "172.16.4.12",
                        isNsfwModerated = false,
                        nsfwScore = 0.05f,
                        viewsCount = 192,
                        bandwidthConsumedBytes = 170880000L
                    ),
                    UploadedMedia(
                        title = "Deep Space Nebula Art",
                        slug = "deep-space-nebula",
                        directUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1600&auto=format&fit=crop&q=80",
                        thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400&auto=format&fit=crop&q=80",
                        fileSizeBytes = 4820000L,
                        fileExtension = "jpg",
                        dimensions = "4096x2304",
                        uploadTimestamp = now - 3600000L * 60,
                        expiryOption = "NEVER",
                        compressionLevel = "ORIGINAL",
                        storageProvider = "AWS S3",
                        uploaderIp = "192.168.1.88",
                        isNsfwModerated = false,
                        nsfwScore = 0.02f,
                        viewsCount = 1420,
                        bandwidthConsumedBytes = 6844400000L
                    )
                )

                for (item in sampleItems) {
                    mediaDao.insertMedia(item)
                }
            }
        }
    }
}
