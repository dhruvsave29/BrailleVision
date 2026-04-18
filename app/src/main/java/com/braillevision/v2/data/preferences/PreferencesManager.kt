package com.braillevision.v2.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserPreferences(
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val autoSpeak: Boolean = false,
    val showGuide: Boolean = true,
    val autoSaveHistory: Boolean = true,
    val spellCorrection: Boolean = true
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val PITCH = floatPreferencesKey("pitch")
        val AUTO_SPEAK = booleanPreferencesKey("auto_speak")
        val SHOW_GUIDE = booleanPreferencesKey("show_guide")
        val AUTO_SAVE_HISTORY = booleanPreferencesKey("auto_save_history")
        val SPELL_CORRECTION = booleanPreferencesKey("spell_correction")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                speechRate = preferences[PreferencesKeys.SPEECH_RATE] ?: 1.0f,
                pitch = preferences[PreferencesKeys.PITCH] ?: 1.0f,
                autoSpeak = preferences[PreferencesKeys.AUTO_SPEAK] ?: false,
                showGuide = preferences[PreferencesKeys.SHOW_GUIDE] ?: true,
                autoSaveHistory = preferences[PreferencesKeys.AUTO_SAVE_HISTORY] ?: true,
                spellCorrection = preferences[PreferencesKeys.SPELL_CORRECTION] ?: true
            )
        }

    suspend fun updateSpeechRate(rate: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SPEECH_RATE] = rate.coerceIn(0.5f, 2.0f)
        }
    }

    suspend fun updatePitch(pitch: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PITCH] = pitch.coerceIn(0.5f, 2.0f)
        }
    }

    suspend fun updateAutoSpeak(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SPEAK] = enabled
        }
    }

    suspend fun updateShowGuide(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_GUIDE] = enabled
        }
    }

    suspend fun updateAutoSaveHistory(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_HISTORY] = enabled
        }
    }

    suspend fun updateSpellCorrection(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SPELL_CORRECTION] = enabled
        }
    }
}
