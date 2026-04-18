package com.braillevision.v2.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braillevision.v2.data.preferences.PreferencesManager
import com.braillevision.v2.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.io.File
import javax.inject.Inject

data class CameraUiState(
    val hasCameraPermission: Boolean = false,
    val flashMode: Int = ImageCapture.FLASH_MODE_OFF,
    val isCapturing: Boolean = false,
    val capturedImageUri: Uri? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()
    
    val userPreferences: StateFlow<UserPreferences> = preferencesManager.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun checkCameraPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        _uiState.update { it.copy(hasCameraPermission = hasPermission) }
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(hasCameraPermission = isGranted) }
    }

    fun toggleFlash() {
        val newFlashMode = when (_uiState.value.flashMode) {
            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
            else -> ImageCapture.FLASH_MODE_OFF
        }
        _uiState.update { it.copy(flashMode = newFlashMode) }
    }

    fun onImageCaptured(file: File) {
        val uri = Uri.fromFile(file)
        _uiState.update { 
            it.copy(
                isCapturing = false,
                capturedImageUri = uri,
                errorMessage = null
            )
        }
    }

    fun onCaptureError(message: String) {
        _uiState.update { 
            it.copy(
                isCapturing = false,
                errorMessage = message
            )
        }
    }

    fun startCapture() {
        _uiState.update { it.copy(isCapturing = true) }
    }

    fun clearCapturedImage() {
        _uiState.update { it.copy(capturedImageUri = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
