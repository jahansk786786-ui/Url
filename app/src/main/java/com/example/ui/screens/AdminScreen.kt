package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AdminSettings
import com.example.data.model.UploadedMedia
import com.example.ui.components.FullscreenPreviewModal
import com.example.ui.components.StatCard
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceLight
import com.example.ui.theme.GlassSurfaceUltraLight
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MediaFilterState
import com.example.util.Formatters

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    mediaList: List<UploadedMedia>,
    adminSettings: AdminSettings,
    adminSubTab: Int,
    filterState: MediaFilterState,
    testConnectionStatus: String?,
    isTestingConnection: Boolean,
    modifier: Modifier = Modifier
) {
    var previewMedia by remember { mutableStateOf<UploadedMedia?>(null) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Sub-Navigation Bar in Frosted Glass Container
        val tabs = listOf(
            Triple("Overview", Icons.Default.Dashboard, 0),
            Triple("Media Manager", Icons.Default.Photo, 1),
            Triple("Storage Providers", Icons.Default.Storage, 2),
            Triple("Security & Limits", Icons.Default.Security, 3),
            Triple("Monetization & Ads", Icons.Default.MonetizationOn, 4)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            ScrollableTabRow(
                selectedTabIndex = adminSubTab,
                containerColor = Color.Transparent,
                edgePadding = 8.dp,
                divider = {},
                indicator = { tabPositions ->
                    if (adminSubTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[adminSubTab])
                                .clip(RoundedCornerShape(4.dp)),
                            color = BrandPrimary,
                            height = 3.dp
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_sub_tabs")
            ) {
                tabs.forEach { (title, icon, index) ->
                    Tab(
                        selected = adminSubTab == index,
                        onClick = { viewModel.setAdminSubTab(index) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (adminSubTab == index) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    title,
                                    fontWeight = if (adminSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (adminSubTab == index) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (adminSubTab) {
                0 -> OverviewDashboardTab(
                    mediaList = mediaList,
                    adminSettings = adminSettings,
                    onPurgeExpired = { viewModel.purgeExpiredMedia() },
                    onNavigateMedia = { viewModel.setAdminSubTab(1) }
                )
                1 -> MediaManagerTab(
                    mediaList = mediaList,
                    filterState = filterState,
                    viewModel = viewModel,
                    onPreviewMedia = { previewMedia = it },
                    onRequestBulkDelete = { showBulkDeleteDialog = true }
                )
                2 -> StorageProviderSettingsTab(
                    adminSettings = adminSettings,
                    testConnectionStatus = testConnectionStatus,
                    isTestingConnection = isTestingConnection,
                    onSaveSettings = { viewModel.updateAdminSettings(it) },
                    onTestConnection = { viewModel.testStorageProviderConnection(it) }
                )
                3 -> SecurityAndLimitsTab(
                    adminSettings = adminSettings,
                    onSaveSettings = { viewModel.updateAdminSettings(it) }
                )
                4 -> MonetizationSettingsTab(
                    adminSettings = adminSettings,
                    onSaveSettings = { viewModel.updateAdminSettings(it) }
                )
            }
        }
    }

    // Fullscreen Preview Modal
    previewMedia?.let { media ->
        FullscreenPreviewModal(
            media = media,
            onDismiss = { previewMedia = null }
        )
    }

    // Bulk Delete Confirmation Dialog
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            containerColor = GlassSurfaceLight,
            title = { Text("Delete ${filterState.selectedMediaIds.size} Images?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove the selected images from local database and storage. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSelectedMedia()
                        showBulkDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OverviewDashboardTab(
    mediaList: List<UploadedMedia>,
    adminSettings: AdminSettings,
    onPurgeExpired: () -> Unit,
    onNavigateMedia: () -> Unit
) {
    val scrollState = rememberScrollState()
    val totalBandwidth = mediaList.sumOf { it.fileSizeBytes + it.bandwidthConsumedBytes }
    val totalPhotos = mediaList.size
    val totalExpired = mediaList.count { it.isExpired }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Quick Summary Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Bandwidth Used",
                value = Formatters.formatBytes(totalBandwidth),
                subValue = "+14.2% this week",
                icon = Icons.Default.DataUsage,
                accentColor = BrandAccent,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Photos",
                value = "$totalPhotos",
                subValue = "$totalExpired expired",
                icon = Icons.Default.Photo,
                accentColor = BrandPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Active Users",
                value = "${adminSettings.activeUsersCount}",
                subValue = "98.4% uptime",
                icon = Icons.Default.People,
                accentColor = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Daily Revenue",
                value = "$${adminSettings.dailyRevenueEstimate}",
                subValue = "eCPM $3.42",
                icon = Icons.Default.AttachMoney,
                accentColor = WarningAmber,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bandwidth Storage Gauge Card in Frosted Glass
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Global CDN Storage Quota",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(BrandPrimaryLight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Provider: ${adminSettings.activeProvider}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val quotaBytes = 50L * 1024 * 1024 * 1024L // 50 GB
                val fraction = (totalBandwidth.toFloat() / quotaBytes).coerceIn(0.05f, 1f)

                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = BrandPrimary,
                    trackColor = GlassSurfaceUltraLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${Formatters.formatBytes(totalBandwidth)} / 50.0 GB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(fraction * 100).toInt()}% consumed",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Maintenance Actions Card in Frosted Glass
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "System Maintenance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onPurgeExpired,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Purge ($totalExpired)")
                    }

                    OutlinedButton(
                        onClick = onNavigateMedia,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BrandPrimary)
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Media Grid", color = BrandPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaManagerTab(
    mediaList: List<UploadedMedia>,
    filterState: MediaFilterState,
    viewModel: MainViewModel,
    onPreviewMedia: (UploadedMedia) -> Unit,
    onRequestBulkDelete: () -> Unit
) {
    val context = LocalContext.current

    // Filter media items
    val filteredList = remember(mediaList, filterState.searchQuery, filterState.filterExtension, filterState.filterProvider) {
        mediaList.filter { media ->
            val matchesQuery = filterState.searchQuery.isBlank() ||
                    media.title.contains(filterState.searchQuery, ignoreCase = true) ||
                    media.slug.contains(filterState.searchQuery, ignoreCase = true) ||
                    media.uploaderIp.contains(filterState.searchQuery, ignoreCase = true)

            val matchesExt = filterState.filterExtension == "ALL" ||
                    media.fileExtension.equals(filterState.filterExtension, ignoreCase = true)

            val matchesProvider = filterState.filterProvider == "ALL" ||
                    media.storageProvider.equals(filterState.filterProvider, ignoreCase = true)

            matchesQuery && matchesExt && matchesProvider
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = filterState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by IP, title, slug...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (filterState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("media_search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GlassSurfaceUltraLight,
                unfocusedContainerColor = GlassSurfaceUltraLight,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = GlassBorderLight
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Extension Filter Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL", "png", "jpg", "webp", "gif", "svg").forEach { ext ->
                val isSelected = filterState.filterExtension == ext
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFilterExtension(ext) },
                    label = { Text(if (ext == "ALL") "All Exts" else ".$ext") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = GlassSurfaceUltraLight,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, if (isSelected) BrandPrimary else GlassBorderLight),
                    modifier = Modifier.testTag("filter_ext_$ext")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bulk Selection Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (filterState.isSelectionMode) BrandPrimary.copy(alpha = 0.12f) else GlassSurfaceUltraLight)
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = filterState.selectedMediaIds.isNotEmpty() && filterState.selectedMediaIds.size == filteredList.size,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            viewModel.selectAllMedia(filteredList.map { it.id })
                        } else {
                            viewModel.clearMediaSelection()
                        }
                    },
                    colors = CheckboxDefaults.colors(checkedColor = BrandPrimary),
                    modifier = Modifier.testTag("select_all_checkbox")
                )
                Text(
                    text = if (filterState.selectedMediaIds.isNotEmpty())
                        "${filterState.selectedMediaIds.size} selected (${filteredList.size} total)"
                    else "${filteredList.size} images",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (filterState.selectedMediaIds.isNotEmpty()) {
                Button(
                    onClick = onRequestBulkDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("bulk_delete_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete (${filterState.selectedMediaIds.size})", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Image Grid View
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No images matched your filters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("media_grid"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { media ->
                    val isSelected = filterState.selectedMediaIds.contains(media.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (filterState.isSelectionMode) {
                                    viewModel.toggleMediaSelection(media.id)
                                } else {
                                    onPreviewMedia(media)
                                }
                            }
                            .testTag("media_card_${media.id}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
                        border = if (isSelected) BorderStroke(2.dp, BrandPrimary) else BorderStroke(1.dp, GlassBorderLight)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .background(Color(0xFF1E1B2E))
                            ) {
                                AsyncImage(
                                    model = media.thumbnailUrl,
                                    contentDescription = media.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Checkbox for multi-select
                                Box(modifier = Modifier.align(Alignment.TopStart).padding(4.dp)) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { viewModel.toggleMediaSelection(media.id) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = BrandPrimary,
                                            uncheckedColor = Color.White
                                        )
                                    )
                                }

                                // Expired Badge
                                if (media.isExpired) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .background(ErrorRed, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Expired", style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = media.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "${Formatters.formatBytes(media.fileSizeBytes)} · .${media.fileExtension}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = "IP: ${media.uploaderIp}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            Formatters.copyToClipboard(context, "Direct Link", media.directUrl)
                                        },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(BrandPrimaryLight, CircleShape)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BrandPrimary, modifier = Modifier.size(14.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteSingleMedia(media.id) },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(ErrorRed.copy(alpha = 0.1f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageProviderSettingsTab(
    adminSettings: AdminSettings,
    testConnectionStatus: String?,
    isTestingConnection: Boolean,
    onSaveSettings: (AdminSettings) -> Unit,
    onTestConnection: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    var activeProvider by remember(adminSettings) { mutableStateOf(adminSettings.activeProvider) }
    var imageKitPublic by remember(adminSettings) { mutableStateOf(adminSettings.imageKitPublicKey) }
    var imageKitPrivate by remember(adminSettings) { mutableStateOf(adminSettings.imageKitPrivateKey) }
    var imageKitUrl by remember(adminSettings) { mutableStateOf(adminSettings.imageKitUrlEndpoint) }

    var cloudinaryName by remember(adminSettings) { mutableStateOf(adminSettings.cloudinaryCloudName) }
    var cloudinaryKey by remember(adminSettings) { mutableStateOf(adminSettings.cloudinaryApiKey) }
    var cloudinarySecret by remember(adminSettings) { mutableStateOf(adminSettings.cloudinaryApiSecret) }

    var firebaseBucket by remember(adminSettings) { mutableStateOf(adminSettings.firebaseBucket) }
    var firebaseProject by remember(adminSettings) { mutableStateOf(adminSettings.firebaseProjectId) }

    var awsBucket by remember(adminSettings) { mutableStateOf(adminSettings.awsS3Bucket) }
    var awsRegion by remember(adminSettings) { mutableStateOf(adminSettings.awsS3Region) }
    var awsAccessKey by remember(adminSettings) { mutableStateOf(adminSettings.awsS3AccessKey) }
    var awsSecretKey by remember(adminSettings) { mutableStateOf(adminSettings.awsS3SecretKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Active Storage Provider",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select default CDN backend for incoming uploads and direct link routing",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Storage Provider Cards
        val providers = listOf(
            Triple("ImageKit", "Realtime URL transforms & global CDN", BrandPrimary),
            Triple("Cloudinary", "AI media optimization & compression", BrandAccent),
            Triple("Firebase Storage", "Scalable Google Cloud Storage bucket", WarningAmber),
            Triple("AWS S3", "Amazon Web Services S3 object storage", InfoBlue)
        )

        providers.forEach { (name, desc, color) ->
            val isSelected = activeProvider == name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { activeProvider = name }
                    .testTag("provider_card_$name"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) color.copy(alpha = 0.12f) else GlassSurfaceLight
                ),
                border = if (isSelected) BorderStroke(2.dp, color) else BorderStroke(1.dp, GlassBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = color)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Provider Credentials Editor in Frosted Glass
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "$activeProvider API Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                val textFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassSurfaceUltraLight,
                    unfocusedContainerColor = GlassSurfaceUltraLight,
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = GlassBorderLight
                )

                when (activeProvider) {
                    "ImageKit" -> {
                        OutlinedTextField(
                            value = imageKitPublic,
                            onValueChange = { imageKitPublic = it },
                            label = { Text("Public Key") },
                            modifier = Modifier.fillMaxWidth().testTag("input_ik_public"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = imageKitPrivate,
                            onValueChange = { imageKitPrivate = it },
                            label = { Text("Private Key") },
                            modifier = Modifier.fillMaxWidth().testTag("input_ik_private"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = imageKitUrl,
                            onValueChange = { imageKitUrl = it },
                            label = { Text("URL Endpoint") },
                            modifier = Modifier.fillMaxWidth().testTag("input_ik_endpoint"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                    }
                    "Cloudinary" -> {
                        OutlinedTextField(
                            value = cloudinaryName,
                            onValueChange = { cloudinaryName = it },
                            label = { Text("Cloud Name") },
                            modifier = Modifier.fillMaxWidth().testTag("input_cld_name"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cloudinaryKey,
                            onValueChange = { cloudinaryKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth().testTag("input_cld_key"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cloudinarySecret,
                            onValueChange = { cloudinarySecret = it },
                            label = { Text("API Secret") },
                            modifier = Modifier.fillMaxWidth().testTag("input_cld_secret"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                    }
                    "Firebase Storage" -> {
                        OutlinedTextField(
                            value = firebaseBucket,
                            onValueChange = { firebaseBucket = it },
                            label = { Text("Storage Bucket") },
                            modifier = Modifier.fillMaxWidth().testTag("input_fb_bucket"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = firebaseProject,
                            onValueChange = { firebaseProject = it },
                            label = { Text("Project ID") },
                            modifier = Modifier.fillMaxWidth().testTag("input_fb_project"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                    }
                    "AWS S3" -> {
                        OutlinedTextField(
                            value = awsBucket,
                            onValueChange = { awsBucket = it },
                            label = { Text("S3 Bucket Name") },
                            modifier = Modifier.fillMaxWidth().testTag("input_s3_bucket"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = awsRegion,
                            onValueChange = { awsRegion = it },
                            label = { Text("Region") },
                            modifier = Modifier.fillMaxWidth().testTag("input_s3_region"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = awsAccessKey,
                            onValueChange = { awsAccessKey = it },
                            label = { Text("Access Key ID") },
                            modifier = Modifier.fillMaxWidth().testTag("input_s3_access"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = awsSecretKey,
                            onValueChange = { awsSecretKey = it },
                            label = { Text("Secret Access Key") },
                            modifier = Modifier.fillMaxWidth().testTag("input_s3_secret"),
                            shape = RoundedCornerShape(14.dp),
                            colors = textFieldColors,
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test Connection Feedback
                testConnectionStatus?.let { status ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SuccessGreen.copy(alpha = 0.12f))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onTestConnection(activeProvider) },
                        enabled = !isTestingConnection,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("test_connection_button")
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test API")
                        }
                    }

                    Button(
                        onClick = {
                            val updated = adminSettings.copy(
                                activeProvider = activeProvider,
                                imageKitPublicKey = imageKitPublic,
                                imageKitPrivateKey = imageKitPrivate,
                                imageKitUrlEndpoint = imageKitUrl,
                                cloudinaryCloudName = cloudinaryName,
                                cloudinaryApiKey = cloudinaryKey,
                                cloudinaryApiSecret = cloudinarySecret,
                                firebaseBucket = firebaseBucket,
                                firebaseProjectId = firebaseProject,
                                awsS3Bucket = awsBucket,
                                awsS3Region = awsRegion,
                                awsS3AccessKey = awsAccessKey,
                                awsS3SecretKey = awsSecretKey
                            )
                            onSaveSettings(updated)
                        },
                        modifier = Modifier.weight(1f).testTag("save_provider_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Config")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecurityAndLimitsTab(
    adminSettings: AdminSettings,
    onSaveSettings: (AdminSettings) -> Unit
) {
    val scrollState = rememberScrollState()

    var rateLimit by remember(adminSettings) { mutableStateOf(adminSettings.ipRateLimitPerHour.toFloat()) }
    var maxFileSizeMb by remember(adminSettings) { mutableStateOf((adminSettings.maxFileSizeBytes / (1024 * 1024)).toFloat()) }
    var allowPng by remember(adminSettings) { mutableStateOf(adminSettings.allowedPng) }
    var allowJpg by remember(adminSettings) { mutableStateOf(adminSettings.allowedJpg) }
    var allowWebp by remember(adminSettings) { mutableStateOf(adminSettings.allowedWebp) }
    var allowGif by remember(adminSettings) { mutableStateOf(adminSettings.allowedGif) }
    var allowSvg by remember(adminSettings) { mutableStateOf(adminSettings.allowedSvg) }
    var autoNsfw by remember(adminSettings) { mutableStateOf(adminSettings.autoNsfwModeration) }
    var nsfwThreshold by remember(adminSettings) { mutableStateOf(adminSettings.nsfwThreshold) }
    var blockNsfw by remember(adminSettings) { mutableStateOf(adminSettings.blockOnNsfw) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Section 1: IP Rate Limiting
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "IP Rate Limiting", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${rateLimit.toInt()} uploads / hr", fontWeight = FontWeight.Bold, color = BrandPrimary)
                }
                Text(
                    text = "Protects storage against DDoS and spam abuse per client IP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = rateLimit,
                    onValueChange = { rateLimit = it },
                    valueRange = 5f..100f,
                    steps = 18,
                    colors = SliderDefaults.colors(thumbColor = BrandPrimary, activeTrackColor = BrandPrimary),
                    modifier = Modifier.testTag("slider_rate_limit")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 2: Max File Size Caps
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Max File Size Cap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${maxFileSizeMb.toInt()} MB", fontWeight = FontWeight.Bold, color = BrandAccent)
                }
                Text(
                    text = "Enforces maximum allowed upload payload per image",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = maxFileSizeMb,
                    onValueChange = { maxFileSizeMb = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    colors = SliderDefaults.colors(thumbColor = BrandAccent, activeTrackColor = BrandAccent),
                    modifier = Modifier.testTag("slider_max_size")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 3: Allowed Extension Toggles
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Allowed File Extensions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Toggle acceptable media MIME types",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = allowPng,
                        onClick = { allowPng = !allowPng },
                        label = { Text(".png") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                        border = BorderStroke(1.dp, if (allowPng) BrandPrimary else GlassBorderLight),
                        modifier = Modifier.testTag("chip_allow_png")
                    )
                    FilterChip(
                        selected = allowJpg,
                        onClick = { allowJpg = !allowJpg },
                        label = { Text(".jpg / .jpeg") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                        border = BorderStroke(1.dp, if (allowJpg) BrandPrimary else GlassBorderLight),
                        modifier = Modifier.testTag("chip_allow_jpg")
                    )
                    FilterChip(
                        selected = allowWebp,
                        onClick = { allowWebp = !allowWebp },
                        label = { Text(".webp") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                        border = BorderStroke(1.dp, if (allowWebp) BrandPrimary else GlassBorderLight),
                        modifier = Modifier.testTag("chip_allow_webp")
                    )
                    FilterChip(
                        selected = allowGif,
                        onClick = { allowGif = !allowGif },
                        label = { Text(".gif") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                        border = BorderStroke(1.dp, if (allowGif) BrandPrimary else GlassBorderLight),
                        modifier = Modifier.testTag("chip_allow_gif")
                    )
                    FilterChip(
                        selected = allowSvg,
                        onClick = { allowSvg = !allowSvg },
                        label = { Text(".svg") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = Color.White),
                        border = BorderStroke(1.dp, if (allowSvg) BrandPrimary else GlassBorderLight),
                        modifier = Modifier.testTag("chip_allow_svg")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 4: Auto-NSFW Moderation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Auto-NSFW AI Moderation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Automatic heuristic content safety inspection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoNsfw,
                        onCheckedChange = { autoNsfw = it },
                        modifier = Modifier.testTag("switch_auto_nsfw")
                    )
                }

                if (autoNsfw) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sensitivity Threshold", style = MaterialTheme.typography.bodySmall)
                        Text(text = "${(nsfwThreshold * 100).toInt()}%", fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = nsfwThreshold,
                        onValueChange = { nsfwThreshold = it },
                        valueRange = 0.3f..0.95f,
                        modifier = Modifier.testTag("slider_nsfw_threshold")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Reject & Block Uploads (vs Flag)", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = blockNsfw,
                            onCheckedChange = { blockNsfw = it }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Security Settings
        Button(
            onClick = {
                val updated = adminSettings.copy(
                    ipRateLimitPerHour = rateLimit.toInt(),
                    maxFileSizeBytes = maxFileSizeMb.toLong() * 1024 * 1024L,
                    allowedPng = allowPng,
                    allowedJpg = allowJpg,
                    allowedWebp = allowWebp,
                    allowedGif = allowGif,
                    allowedSvg = allowSvg,
                    autoNsfwModeration = autoNsfw,
                    nsfwThreshold = nsfwThreshold,
                    blockOnNsfw = blockNsfw
                )
                onSaveSettings(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_security_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Security Policy", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun MonetizationSettingsTab(
    adminSettings: AdminSettings,
    onSaveSettings: (AdminSettings) -> Unit
) {
    val scrollState = rememberScrollState()

    var enableInterstitial by remember(adminSettings) { mutableStateOf(adminSettings.enableInterstitialAds) }
    var timerSeconds by remember(adminSettings) { mutableStateOf(adminSettings.interstitialTimerSeconds.toFloat()) }
    var adSlotId by remember(adminSettings) { mutableStateOf(adminSettings.adBannerSlotId) }
    var adScriptCode by remember(adminSettings) { mutableStateOf(adminSettings.adScriptCode) }
    var enableLandingPage by remember(adminSettings) { mutableStateOf(adminSettings.enableAdLandingPage) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = GlassSurfaceUltraLight,
        unfocusedContainerColor = GlassSurfaceUltraLight,
        focusedBorderColor = BrandPrimary,
        unfocusedBorderColor = GlassBorderLight
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Card 1: Interstitial Ads before link generation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Interstitial Ad Gate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Displays high-yield sponsor ad before generating download/embed links",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enableInterstitial,
                        onCheckedChange = { enableInterstitial = it },
                        modifier = Modifier.testTag("switch_interstitial")
                    )
                }

                if (enableInterstitial) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Countdown Duration", style = MaterialTheme.typography.bodySmall)
                        Text(text = "${timerSeconds.toInt()} seconds", fontWeight = FontWeight.Bold, color = WarningAmber)
                    }

                    Slider(
                        value = timerSeconds,
                        onValueChange = { timerSeconds = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(thumbColor = WarningAmber, activeTrackColor = WarningAmber),
                        modifier = Modifier.testTag("slider_interstitial_timer")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 2: Ad Units & Script Code
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Ad Network & Unit Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Configure Google AdSense / AdMob unit identifiers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = adSlotId,
                    onValueChange = { adSlotId = it },
                    label = { Text("Banner Slot / Unit ID") },
                    modifier = Modifier.fillMaxWidth().testTag("input_ad_slot"),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = adScriptCode,
                    onValueChange = { adScriptCode = it },
                    label = { Text("Custom Ad Script Header") },
                    modifier = Modifier.fillMaxWidth().testTag("input_ad_script"),
                    shape = RoundedCornerShape(14.dp),
                    colors = textFieldColors,
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 3: Ad-Supported Landing Pages
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceLight),
            border = BorderStroke(1.dp, GlassBorderLight)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Ad-Supported Landing Pages", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Routes public slugs (cloudpix.io/i/{slug}) through monetization landing view",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enableLandingPage,
                        onCheckedChange = { enableLandingPage = it },
                        modifier = Modifier.testTag("switch_landing_ads")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Monetization Settings
        Button(
            onClick = {
                val updated = adminSettings.copy(
                    enableInterstitialAds = enableInterstitial,
                    interstitialTimerSeconds = timerSeconds.toInt(),
                    adBannerSlotId = adSlotId,
                    adScriptCode = adScriptCode,
                    enableAdLandingPage = enableLandingPage
                )
                onSaveSettings(updated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_monetization_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Monetization Configuration", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

