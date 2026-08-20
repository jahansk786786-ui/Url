package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_settings")
data class AdminSettings(
    @PrimaryKey
    val id: Int = 1,
    // Active Storage Provider
    val activeProvider: String = "ImageKit", // ImageKit, Cloudinary, Firebase Storage, AWS S3
    
    // ImageKit Credentials
    val imageKitPublicKey: String = "public_ik_live_794830184a",
    val imageKitPrivateKey: String = "private_ik_sec_993821049b",
    val imageKitUrlEndpoint: String = "https://ik.imagekit.io/cloudpix_hub",
    
    // Cloudinary Credentials
    val cloudinaryCloudName: String = "cloudpix-cdn",
    val cloudinaryApiKey: String = "849201948291039",
    val cloudinaryApiSecret: String = "kL92mNvQp8R1sTtUvW",
    
    // Firebase Storage
    val firebaseBucket: String = "cloudpix-media-vault.appspot.com",
    val firebaseProjectId: String = "cloudpix-pro-2026",
    
    // AWS S3
    val awsS3Bucket: String = "cloudpix-global-images",
    val awsS3Region: String = "us-east-1",
    val awsS3AccessKey: String = "AKIAIOSFODNN7EXAMPLE",
    val awsS3SecretKey: String = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",

    // Security & Limits
    val ipRateLimitPerHour: Int = 25, // max uploads per hour per IP
    val maxFileSizeBytes: Long = 10 * 1024 * 1024L, // Default 10 MB
    val allowedPng: Boolean = true,
    val allowedJpg: Boolean = true,
    val allowedWebp: Boolean = true,
    val allowedGif: Boolean = true,
    val allowedSvg: Boolean = true,
    val autoNsfwModeration: Boolean = true,
    val nsfwThreshold: Float = 0.70f,
    val blockOnNsfw: Boolean = false, // if true, reject upload; if false, flag & blur

    // Monetization & Ad Settings
    val enableInterstitialAds: Boolean = true,
    val interstitialTimerSeconds: Int = 3,
    val adBannerSlotId: String = "ca-app-pub-3940256099942544/6300978111",
    val adScriptCode: String = "<script async src=\"https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>",
    val enableAdLandingPage: Boolean = true,
    val dailyRevenueEstimate: Double = 428.50,
    val activeUsersCount: Int = 1845
)
