package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AdminSettings
import com.example.data.model.UploadedMedia
import com.example.ui.components.CodeBox
import com.example.ui.components.FullscreenPreviewModal
import com.example.ui.components.InterstitialAdModal
import com.example.ui.components.QrCodeDisplay
import com.example.ui.components.SocialShareSheet
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceLight
import com.example.ui.theme.GlassSurfaceUltraLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UploadUiState
import com.example.util.Formatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadScreen(
    viewModel: MainViewModel,
    uploadState: UploadUiState,
    adminSettings: AdminSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = it.toString()
            val ext = if (path.contains(".png", true)) "png" else if (path.contains(".webp", true)) "webp" else "jpg"
            viewModel.setSelectedLocalImage(path, 3200000L, ext)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Hero Top Banner with active provider status
            HeroHeaderCard(activeProvider = adminSettings.activeProvider)

            Spacer(modifier = Modifier.height(16.dp))

            // Check if last upload result is available
            if (uploadState.lastUploadedMedia != null) {
                ResultPanel(
                    media = uploadState.lastUploadedMedia,
                    onUploadAnother = { viewModel.dismissResultAndUploadAnother() },
                    onPreview = { viewModel.showFullscreenPreview(uploadState.lastUploadedMedia) },
                    onShare = { viewModel.setSocialShareSheetVisible(true) }
                )
            } else {
                // Upload Form
                UploadForm(
                    viewModel = viewModel,
                    uploadState = uploadState,
                    adminSettings = adminSettings,
                    onPickFile = { filePickerLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Modals
        if (uploadState.showInterstitialModal && uploadState.lastUploadedMedia == null) {
            InterstitialAdModal(
                secondsRemaining = uploadState.interstitialSecondsRemaining,
                onSkip = {
                    // Skip action triggered
                }
            )
        }

        uploadState.activeFullscreenPreviewMedia?.let { media ->
            FullscreenPreviewModal(
                media = media,
                onDismiss = { viewModel.showFullscreenPreview(null) }
            )
        }

        if (uploadState.showSocialShareSheet && uploadState.lastUploadedMedia != null) {
            SocialShareSheet(
                url = uploadState.lastUploadedMedia.directUrl,
                title = uploadState.lastUploadedMedia.title,
                onDismiss = { viewModel.setSocialShareSheetVisible(false) }
            )
        }
    }
}

@Composable
fun HeroHeaderCard(activeProvider: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BrandPrimary, BrandPrimaryDark)
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, GlassBorderLight, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CloudPix Host",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Instant CDN Hosting & Embeds",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                SuccessGreen.copy(alpha = 0.12f),
                                RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(SuccessGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeProvider,
                                style = MaterialTheme.typography.labelSmall,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadForm(
    viewModel: MainViewModel,
    uploadState: UploadUiState,
    adminSettings: AdminSettings,
    onPickFile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upload_form_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Source Mode Tabs (File vs URL)
            TabRow(
                selectedTabIndex = if (uploadState.uploadMode == "FILE") 0 else 1,
                containerColor = GlassSurfaceUltraLight,
                contentColor = BrandPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = uploadState.uploadMode == "FILE",
                    onClick = { viewModel.setUploadMode("FILE") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Device File", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_device_file")
                )
                Tab(
                    selected = uploadState.uploadMode == "URL",
                    onClick = { viewModel.setUploadMode("URL") },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Image URL", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_image_url")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Zone
            if (uploadState.uploadMode == "FILE") {
                if (uploadState.selectedImageUri != null) {
                    // Selected Image Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = uploadState.selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.clearSelectedImage() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color(0x99000000), CircleShape)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .background(Color(0xCC0F172A), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uploadState.detectedExtension.uppercase()} · ~${Formatters.formatBytes(uploadState.estimatedOriginalSize)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Drag & Drop / Tap Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, BrandPrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .background(BrandPrimaryLight.copy(alpha = 0.3f))
                            .clickable { onPickFile() }
                            .padding(16.dp)
                            .testTag("upload_drop_zone"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(BrandPrimaryLight, CircleShape)
                                    .border(1.dp, GlassBorderLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = BrandPrimaryDark,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Drag & Drop or Tap to Browse",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Supports PNG, JPG, WebP, GIF, SVG · Max ${adminSettings.maxFileSizeBytes / (1024 * 1024)}MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Direct URL Input Zone
                Column {
                    OutlinedTextField(
                        value = uploadState.inputImageUrl,
                        onValueChange = { viewModel.setInputImageUrl(it) },
                        label = { Text("Direct Image URL") },
                        placeholder = { Text("https://example.com/photo.jpg") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = BrandPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_image_url"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GlassSurfaceUltraLight,
                            unfocusedContainerColor = GlassSurfaceUltraLight
                        ),
                        singleLine = true
                    )

                    if (uploadState.inputImageUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = uploadState.inputImageUrl,
                                contentDescription = "URL Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata & Custom Slug
            OutlinedTextField(
                value = uploadState.imageTitle,
                onValueChange = { viewModel.setImageTitle(it) },
                label = { Text("Image Title (Optional)") },
                placeholder = { Text("e.g. Vacation Sunset 2026") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_image_title"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassSurfaceUltraLight,
                    unfocusedContainerColor = GlassSurfaceUltraLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uploadState.customSlug,
                onValueChange = { viewModel.setCustomSlug(it) },
                label = { Text("Custom Link Slug") },
                placeholder = { Text("e.g. sunset-highres") },
                prefix = { Text("cloudpix.io/i/", color = BrandPrimary, fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_custom_slug"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassSurfaceUltraLight,
                    unfocusedContainerColor = GlassSurfaceUltraLight
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Settings: Link Expiry
            Text(
                text = "Link Expiry",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "NEVER" to "Never",
                    "10_MINS" to "10 mins",
                    "24_HOURS" to "24 hours",
                    "7_DAYS" to "7 days"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = uploadState.expiryOption == key,
                        onClick = { viewModel.setExpiryOption(key) },
                        label = { Text(label) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = GlassSurfaceUltraLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uploadState.expiryOption == key,
                            borderColor = GlassBorderLight
                        ),
                        modifier = Modifier.testTag("expiry_chip_$key")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Settings: Compression Level
            Text(
                text = "Compression Level",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ORIGINAL" to "Original (100%)",
                    "HIGH_80" to "High (80%)",
                    "BALANCED_60" to "Balanced (60%)",
                    "COMPACT_40" to "Compact (40%)"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = uploadState.compressionLevel == key,
                        onClick = { viewModel.setCompressionLevel(key) },
                        label = { Text(label) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandAccent,
                            selectedLabelColor = Color.White,
                            containerColor = GlassSurfaceUltraLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = uploadState.compressionLevel == key,
                            borderColor = GlassBorderLight
                        ),
                        modifier = Modifier.testTag("compression_chip_$key")
                    )
                }
            }

            // Error display
            if (uploadState.uploadError != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = ErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uploadState.uploadError,
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed
                        )
                    }
                }
            }

            // Progress bar
            if (uploadState.isUploading) {
                Spacer(modifier = Modifier.height(14.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Uploading to ${adminSettings.activeProvider}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandPrimary
                        )
                        Text(
                            text = "${(uploadState.uploadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { uploadState.uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upload Submit Button
            Button(
                onClick = { viewModel.triggerUpload() },
                enabled = !uploadState.isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("upload_submit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                if (uploadState.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Uploading Image...")
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload & Generate Instant Links",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ResultPanel(
    media: UploadedMedia,
    onUploadAnother: () -> Unit,
    onPreview: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_panel_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderLight)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Success header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Successful!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }

                Button(
                    onClick = onUploadAnother,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimaryLight,
                        contentColor = BrandPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("upload_another_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload Another", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Thumbnail Preview Card with Quick Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = media.thumbnailUrl,
                    contentDescription = media.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay actions
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPreview,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC0F172A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("live_preview_button")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Preview", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("social_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Output Box 1: Direct Link
            CodeBox(
                title = "Direct Image Link",
                code = media.directUrl,
                tag = "URL",
                onCopy = {
                    Formatters.copyToClipboard(context, "Direct Link", media.directUrl)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Output Box 2: HTML Embed Code
            CodeBox(
                title = "HTML Embed Code",
                code = media.htmlEmbedCode,
                tag = "HTML",
                onCopy = {
                    Formatters.copyToClipboard(context, "HTML Embed", media.htmlEmbedCode)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Output Box 3: Markdown Code
            CodeBox(
                title = "Markdown Code",
                code = media.markdownCode,
                tag = "MD",
                onCopy = {
                    Formatters.copyToClipboard(context, "Markdown Code", media.markdownCode)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Auto-Generated QR Code Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("qr_code_section"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlassSurfaceUltraLight
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Instant QR Code",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Scan to instantly open on any smartphone or tablet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    QrCodeDisplay(text = media.directUrl, sizeDp = 160)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                Formatters.copyToClipboard(context, "QR URL", media.directUrl)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Copy QR Destination URL", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

