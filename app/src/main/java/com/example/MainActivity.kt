package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassGradientEnd
import com.example.ui.theme.GlassGradientMid
import com.example.ui.theme.GlassGradientStart
import com.example.ui.theme.GlassSurfaceLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CloudPixApp()
            }
        }
    }
}

@Composable
fun CloudPixApp(viewModel: MainViewModel = viewModel()) {
    val navTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val adminSubTab by viewModel.adminSubTab.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val allMedia by viewModel.allMedia.collectAsStateWithLifecycle()
    val adminSettings by viewModel.adminSettings.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val testConnectionStatus by viewModel.testConnectionStatus.collectAsStateWithLifecycle()
    val isTestingConnection by viewModel.isTestingConnection.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        GlassGradientStart,
                        GlassGradientMid,
                        GlassGradientEnd
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(GlassSurfaceLight)
                        .border(1.dp, GlassBorderLight, RoundedCornerShape(24.dp))
                ) {
                    NavigationBar(
                        modifier = Modifier.testTag("bottom_navigation_bar"),
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = navTab == 0,
                            onClick = { viewModel.setNavTab(0) },
                            icon = {
                                Icon(
                                    imageVector = if (navTab == 0) Icons.Filled.CloudUpload else Icons.Outlined.CloudUpload,
                                    contentDescription = "Upload Form"
                                )
                            },
                            label = { Text("Upload", fontWeight = if (navTab == 0) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandPrimaryDark,
                                selectedTextColor = BrandPrimaryDark,
                                indicatorColor = BrandPrimaryLight,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_upload")
                        )

                        NavigationBarItem(
                            selected = navTab == 1,
                            onClick = { viewModel.setNavTab(1) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (allMedia.isNotEmpty()) {
                                            Badge(
                                                containerColor = BrandPrimary,
                                                contentColor = Color.White
                                            ) {
                                                Text("${allMedia.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (navTab == 1) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                        contentDescription = "Admin Control Panel"
                                    )
                                }
                            },
                            label = { Text("Admin Panel", fontWeight = if (navTab == 1) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandPrimaryDark,
                                selectedTextColor = BrandPrimaryDark,
                                indicatorColor = BrandPrimaryLight,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_admin")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (navTab) {
                    0 -> UploadScreen(
                        viewModel = viewModel,
                        uploadState = uploadState,
                        adminSettings = adminSettings
                    )
                    1 -> AdminScreen(
                        viewModel = viewModel,
                        mediaList = allMedia,
                        adminSettings = adminSettings,
                        adminSubTab = adminSubTab,
                        filterState = filterState,
                        testConnectionStatus = testConnectionStatus,
                        isTestingConnection = isTestingConnection
                    )
                }
            }
        }
    }
}


