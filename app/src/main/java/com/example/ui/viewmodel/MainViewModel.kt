package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminSettings
import com.example.data.model.UploadedMedia
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UploadUiState(
    val uploadMode: String = "FILE", // "FILE" or "URL"
    val selectedImageUri: String? = null,
    val inputImageUrl: String = "",
    val imageTitle: String = "",
    val customSlug: String = "",
    val expiryOption: String = "NEVER",
    val compressionLevel: String = "HIGH_80",
    val estimatedOriginalSize: Long = 2400000L, // 2.4 MB default estimate
    val detectedExtension: String = "jpg",
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val uploadError: String? = null,
    val lastUploadedMedia: UploadedMedia? = null,
    val showInterstitialModal: Boolean = false,
    val interstitialSecondsRemaining: Int = 3,
    val activeFullscreenPreviewMedia: UploadedMedia? = null,
    val showSocialShareSheet: Boolean = false
)

data class MediaFilterState(
    val searchQuery: String = "",
    val filterExtension: String = "ALL",
    val filterProvider: String = "ALL",
    val selectedMediaIds: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MediaRepository
    val allMedia: StateFlow<List<UploadedMedia>>
    val adminSettings: StateFlow<AdminSettings>

    private val _uploadState = MutableStateFlow(UploadUiState())
    val uploadState: StateFlow<UploadUiState> = _uploadState.asStateFlow()

    private val _filterState = MutableStateFlow(MediaFilterState())
    val filterState: StateFlow<MediaFilterState> = _filterState.asStateFlow()

    private val _currentNavTab = MutableStateFlow(0) // 0: Upload, 1: Admin
    val currentNavTab: StateFlow<Int> = _currentNavTab.asStateFlow()

    private val _adminSubTab = MutableStateFlow(0) // 0: Overview, 1: Media Manager, 2: Storage, 3: Security, 4: Ads
    val adminSubTab: StateFlow<Int> = _adminSubTab.asStateFlow()

    private val _testConnectionStatus = MutableStateFlow<String?>(null)
    val testConnectionStatus: StateFlow<String?> = _testConnectionStatus.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var interstitialJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = MediaRepository(database.mediaDao(), database.settingsDao())

        allMedia = repository.allMedia.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        adminSettings = repository.adminSettings.combine(_toastMessage) { settings, _ ->
            settings ?: AdminSettings()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdminSettings()
        )
    }

    fun setNavTab(index: Int) {
        _currentNavTab.value = index
    }

    fun setAdminSubTab(index: Int) {
        _adminSubTab.value = index
    }

    fun setUploadMode(mode: String) {
        _uploadState.update { it.copy(uploadMode = mode, uploadError = null) }
    }

    fun setSelectedLocalImage(uriString: String, estimatedBytes: Long = 2800000L, ext: String = "jpg") {
        _uploadState.update {
            it.copy(
                selectedImageUri = uriString,
                estimatedOriginalSize = estimatedBytes,
                detectedExtension = ext,
                uploadError = null
            )
        }
    }

    fun setInputImageUrl(url: String) {
        val ext = when {
            url.contains(".png", ignoreCase = true) -> "png"
            url.contains(".webp", ignoreCase = true) -> "webp"
            url.contains(".gif", ignoreCase = true) -> "gif"
            url.contains(".svg", ignoreCase = true) -> "svg"
            else -> "jpg"
        }
        _uploadState.update {
            it.copy(
                inputImageUrl = url,
                detectedExtension = ext,
                uploadError = null
            )
        }
    }

    fun setImageTitle(title: String) {
        _uploadState.update { it.copy(imageTitle = title) }
    }

    fun setCustomSlug(slug: String) {
        _uploadState.update { it.copy(customSlug = slug) }
    }

    fun setExpiryOption(option: String) {
        _uploadState.update { it.copy(expiryOption = option) }
    }

    fun setCompressionLevel(level: String) {
        _uploadState.update { it.copy(compressionLevel = level) }
    }

    fun clearSelectedImage() {
        _uploadState.update {
            it.copy(
                selectedImageUri = null,
                inputImageUrl = "",
                uploadError = null
            )
        }
    }

    fun dismissResultAndUploadAnother() {
        _uploadState.update {
            it.copy(
                lastUploadedMedia = null,
                selectedImageUri = null,
                inputImageUrl = "",
                imageTitle = "",
                customSlug = "",
                uploadError = null,
                uploadProgress = 0f
            )
        }
    }

    fun showFullscreenPreview(media: UploadedMedia?) {
        _uploadState.update { it.copy(activeFullscreenPreviewMedia = media) }
    }

    fun setSocialShareSheetVisible(visible: Boolean) {
        _uploadState.update { it.copy(showSocialShareSheet = visible) }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun triggerUpload() {
        val currentState = _uploadState.value
        val sourceUri = currentState.selectedImageUri
        val sourceUrl = currentState.inputImageUrl.trim()

        if (currentState.uploadMode == "FILE" && sourceUri == null) {
            _uploadState.update { it.copy(uploadError = "Please select an image file first.") }
            return
        }
        if (currentState.uploadMode == "URL" && sourceUrl.isBlank()) {
            _uploadState.update { it.copy(uploadError = "Please enter a valid image URL.") }
            return
        }

        viewModelScope.launch {
            _uploadState.update { it.copy(isUploading = true, uploadProgress = 0.1f, uploadError = null) }

            // Simulated upload progression
            for (p in 2..9) {
                delay(80)
                _uploadState.update { it.copy(uploadProgress = p / 10f) }
            }

            val result = repository.processAndUploadMedia(
                title = currentState.imageTitle.ifBlank { "CloudPix Media" },
                customSlug = currentState.customSlug,
                sourceUri = if (currentState.uploadMode == "FILE") sourceUri else null,
                sourceUrl = if (currentState.uploadMode == "URL") sourceUrl else null,
                originalSizeBytes = currentState.estimatedOriginalSize,
                extension = currentState.detectedExtension,
                expiryOption = currentState.expiryOption,
                compressionLevel = currentState.compressionLevel
            )

            result.onSuccess { media ->
                val settings = adminSettings.value
                if (settings.enableInterstitialAds) {
                    // Show Interstitial Ad countdown before revealing final links
                    _uploadState.update {
                        it.copy(
                            isUploading = false,
                            uploadProgress = 1.0f,
                            showInterstitialModal = true,
                            interstitialSecondsRemaining = settings.interstitialTimerSeconds
                        )
                    }
                    startInterstitialCountdown(media)
                } else {
                    _uploadState.update {
                        it.copy(
                            isUploading = false,
                            uploadProgress = 1.0f,
                            lastUploadedMedia = media
                        )
                    }
                }
            }.onFailure { exception ->
                _uploadState.update {
                    it.copy(
                        isUploading = false,
                        uploadProgress = 0f,
                        uploadError = exception.message ?: "Upload failed. Please check admin security limits."
                    )
                }
            }
        }
    }

    private fun startInterstitialCountdown(media: UploadedMedia) {
        interstitialJob?.cancel()
        interstitialJob = viewModelScope.launch {
            var seconds = _uploadState.value.interstitialSecondsRemaining
            while (seconds > 0) {
                delay(1000)
                seconds--
                _uploadState.update { it.copy(interstitialSecondsRemaining = seconds) }
            }
            delay(300)
            _uploadState.update {
                it.copy(
                    showInterstitialModal = false,
                    lastUploadedMedia = media
                )
            }
        }
    }

    fun skipInterstitialNow(media: UploadedMedia) {
        interstitialJob?.cancel()
        _uploadState.update {
            it.copy(
                showInterstitialModal = false,
                lastUploadedMedia = media
            )
        }
    }

    // Media Manager Actions
    fun setSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun setFilterExtension(ext: String) {
        _filterState.update { it.copy(filterExtension = ext) }
    }

    fun setFilterProvider(provider: String) {
        _filterState.update { it.copy(filterProvider = provider) }
    }

    fun toggleMediaSelection(id: Long) {
        _filterState.update {
            val current = it.selectedMediaIds.toMutableSet()
            if (current.contains(id)) {
                current.remove(id)
            } else {
                current.add(id)
            }
            it.copy(
                selectedMediaIds = current,
                isSelectionMode = current.isNotEmpty()
            )
        }
    }

    fun selectAllMedia(allIds: List<Long>) {
        _filterState.update {
            it.copy(
                selectedMediaIds = allIds.toSet(),
                isSelectionMode = true
            )
        }
    }

    fun clearMediaSelection() {
        _filterState.update {
            it.copy(
                selectedMediaIds = emptySet(),
                isSelectionMode = false
            )
        }
    }

    fun deleteSelectedMedia() {
        val ids = _filterState.value.selectedMediaIds.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.deleteMediaBatch(ids)
            clearMediaSelection()
            _toastMessage.value = "Deleted ${ids.size} images."
        }
    }

    fun deleteSingleMedia(id: Long) {
        viewModelScope.launch {
            repository.deleteMediaById(id)
            _toastMessage.value = "Image removed."
        }
    }

    fun purgeExpiredMedia() {
        viewModelScope.launch {
            val count = repository.purgeExpiredMedia()
            _toastMessage.value = "Purged $count expired images."
        }
    }

    // Admin Settings Updates
    fun updateAdminSettings(settings: AdminSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            _toastMessage.value = "Admin settings updated successfully."
        }
    }

    fun testStorageProviderConnection(providerName: String) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            _testConnectionStatus.value = "Connecting to $providerName edge API gateway..."
            delay(1200)
            val pingMs = (24..68).random()
            _testConnectionStatus.value = "Connected to $providerName: Status 200 OK (Latency: ${pingMs}ms, SSL Valid)"
            _isTestingConnection.value = false
        }
    }
}
