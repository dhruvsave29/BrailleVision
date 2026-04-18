package com.braillevision.v2.ui.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braillevision.v2.data.local.HistoryDao
import com.braillevision.v2.data.local.HistoryEntity
import com.braillevision.v2.data.preferences.PreferencesManager
import com.braillevision.v2.data.spell.SymSpell
import com.braillevision.v2.data.tflite.YoloDetector
import com.braillevision.v2.domain.usecase.SpeakTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val isLoading: Boolean = false,
    val originalText: String = "",
    val correctedText: String = "",
    val confidence: Float = 0f,
    val characterCount: Int = 0,
    val hasResults: Boolean = false,
    val error: String? = null,
    val savedToHistory: Boolean = false
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val yoloDetector: YoloDetector,
    private val historyDao: HistoryDao,
    private val preferencesManager: PreferencesManager,
    private val speakTextUseCase: SpeakTextUseCase,
    private val symSpell: SymSpell
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    val isSpeaking: StateFlow<Boolean> = speakTextUseCase.isSpeaking
    
    private val userPreferences = preferencesManager.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.braillevision.v2.data.preferences.UserPreferences()
    )
    
    init {
        viewModelScope.launch {
            symSpell.loadDictionary()
        }
    }

    fun processImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            if (!yoloDetector.isReady()) {
                val loaded = yoloDetector.preloadModel()
                if (!loaded) {
                    val errorMsg = yoloDetector.getLoadError() ?: "Failed to load ML model"
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = errorMsg
                        )
                    }
                    return@launch
                }
            }
            
            try {
                val bitmap = loadBitmapFromUriWithRotation(uri)
                if (bitmap == null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load image"
                        )
                    }
                    return@launch
                }
                
                val startTime = System.currentTimeMillis()
                val detections = yoloDetector.detect(bitmap)
                val processingTime = System.currentTimeMillis() - startTime
                
                if (detections.isEmpty()) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            hasResults = false,
                            error = null
                        )
                    }
                    return@launch
                }
                
                val originalText = detections.map { it.character }.joinToString("")
                val correctedText = if (userPreferences.value.spellCorrection) {
                    correctText(originalText)
                } else {
                    originalText
                }
                
                val avgConfidence = if (detections.isNotEmpty()) {
                    detections.map { it.confidence }.average().toFloat()
                } else 0f
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        originalText = originalText,
                        correctedText = correctedText,
                        confidence = avgConfidence,
                        characterCount = detections.size,
                        hasResults = true,
                        error = null
                    )
                }
                
                if (userPreferences.value.autoSpeak) {
                    speakText()
                }
                
                if (userPreferences.value.autoSaveHistory) {
                    saveToHistory(uri)
                }
                
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Processing failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadBitmapFromUriWithRotation(uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // First decode to get dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
            // Decode actual bitmap
            val inputStream2 = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, null)
            inputStream2.close()
            
            if (bitmap == null) return null
            
            // Get rotation from EXIF
            val rotationInputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(rotationInputStream)
            rotationInputStream.close()
            
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val rotationDegrees = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            
            Log.d("ResultViewModel", "Image orientation: $orientation, rotation: $rotationDegrees")
            
            if (rotationDegrees != 0f) {
                val matrix = Matrix().apply { postRotate(rotationDegrees) }
                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                )
                if (rotatedBitmap != bitmap) {
                    bitmap.recycle()
                }
                rotatedBitmap
            } else {
                bitmap
            }
        } catch (e: Exception) {
            Log.e("ResultViewModel", "Error loading bitmap with rotation", e)
            // Fallback to simple loading
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    private fun correctText(text: String): String {
        return if (symSpell.isReady()) {
            symSpell.correctSentence(text)
        } else {
            fallbackSpellCorrect(text)
        }
    }
    
    private fun fallbackSpellCorrect(text: String): String {
        return text
            .replace("helio", "hello")
            .replace("wnrld", "world")
            .replace("brailie", "braille")
            .replace("visin", "vision")
    }

    fun speakText() {
        val text = _uiState.value.correctedText
        if (text.isNotBlank()) {
            speakTextUseCase.speak(text)
        }
    }

    fun saveToHistory(imageUri: Uri?) {
        if (_uiState.value.savedToHistory) return
        
        viewModelScope.launch {
            val state = _uiState.value
            if (!state.hasResults) return@launch
            
            historyDao.insert(
                HistoryEntity(
                    originalText = state.originalText,
                    correctedText = state.correctedText,
                    confidence = state.confidence,
                    characterCount = state.characterCount,
                    imageUri = imageUri?.toString() ?: "",
                    processingTimeMs = 0L
                )
            )
            
            _uiState.update { it.copy(savedToHistory = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
