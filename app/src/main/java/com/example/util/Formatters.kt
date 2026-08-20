package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
        return String.format(Locale.US, "%.1f %s", value, units[digitGroups])
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateShort(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatExpiryCountdown(expiryTimestamp: Long?): String {
        if (expiryTimestamp == null) return "Never expires"
        val diff = expiryTimestamp - System.currentTimeMillis()
        if (diff <= 0) return "Expired"
        val mins = diff / (1000 * 60)
        val hours = mins / 60
        val days = hours / 24
        return when {
            days > 0 -> "Expires in ${days}d ${hours % 24}h"
            hours > 0 -> "Expires in ${hours}h ${mins % 60}m"
            else -> "Expires in ${mins}m"
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareText(context: Context, text: String, title: String = "Share Image Link") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    fun shareToSocial(context: Context, platform: String, text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            when (platform.lowercase()) {
                "whatsapp" -> intent.setPackage("com.whatsapp")
                "twitter", "x" -> intent.setPackage("com.twitter.android")
                "telegram" -> intent.setPackage("org.telegram.messenger")
                "reddit" -> intent.setPackage("com.reddit.frontpage")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to standard share sheet
            shareText(context, text, "Share via $platform")
        }
    }
}
