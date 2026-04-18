package com.braillevision.v2.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braillevision.v2.data.preferences.PreferencesManager
import com.braillevision.v2.data.preferences.UserPreferences
import com.braillevision.v2.data.tflite.YoloDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val yoloDetector: YoloDetector
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesManager.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    val isModelReady: Boolean
        get() = yoloDetector.isReady()

    fun updateSpeechRate(rate: Float) {
        viewModelScope.launch {
            preferencesManager.updateSpeechRate(rate)
        }
    }

    fun updatePitch(pitch: Float) {
        viewModelScope.launch {
            preferencesManager.updatePitch(pitch)
        }
    }

    fun updateAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateAutoSpeak(enabled)
        }
    }

    fun updateShowGuide(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateShowGuide(enabled)
        }
    }

    fun updateAutoSaveHistory(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateAutoSaveHistory(enabled)
        }
    }

    fun updateSpellCorrection(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateSpellCorrection(enabled)
        }
    }
}
