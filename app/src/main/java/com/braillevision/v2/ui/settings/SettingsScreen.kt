package com.braillevision.v2.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braillevision.v2.BuildConfig
import com.braillevision.v2.R
import com.braillevision.v2.ui.components.NeoCard
import com.braillevision.v2.ui.theme.Black
import com.braillevision.v2.ui.theme.Cream
import com.braillevision.v2.ui.theme.OffWhite
import com.braillevision.v2.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsState()
    val isModelReady = viewModel.isModelReady

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpeechSettingsCard(
                speechRate = preferences.speechRate,
                pitch = preferences.pitch,
                onSpeechRateChange = { viewModel.updateSpeechRate(it) },
                onPitchChange = { viewModel.updatePitch(it) }
            )

            RecognitionSettingsCard(
                autoSpeak = preferences.autoSpeak,
                showGuide = preferences.showGuide,
                autoSaveHistory = preferences.autoSaveHistory,
                spellCorrection = preferences.spellCorrection,
                onAutoSpeakChange = { viewModel.updateAutoSpeak(it) },
                onShowGuideChange = { viewModel.updateShowGuide(it) },
                onAutoSaveHistoryChange = { viewModel.updateAutoSaveHistory(it) },
                onSpellCorrectionChange = { viewModel.updateSpellCorrection(it) }
            )

            AppInfoCard(
                isModelReady = isModelReady,
                versionName = BuildConfig.VERSION_NAME
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SpeechSettingsCard(
    speechRate: Float,
    pitch: Float,
    onSpeechRateChange: (Float) -> Unit,
    onPitchChange: (Float) -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Cream
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Speech Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Speech Rate: ${String.format("%.1f", speechRate)}x",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )
            Slider(
                value = speechRate,
                onValueChange = onSpeechRateChange,
                valueRange = 0.5f..2.0f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = Black,
                    activeTrackColor = Yellow,
                    inactiveTrackColor = Black.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pitch: ${String.format("%.1f", pitch)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )
            Slider(
                value = pitch,
                onValueChange = onPitchChange,
                valueRange = 0.5f..2.0f,
                steps = 15,
                colors = SliderDefaults.colors(
                    thumbColor = Black,
                    activeTrackColor = Yellow,
                    inactiveTrackColor = Black.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RecognitionSettingsCard(
    autoSpeak: Boolean,
    showGuide: Boolean,
    autoSaveHistory: Boolean,
    spellCorrection: Boolean,
    onAutoSpeakChange: (Boolean) -> Unit,
    onShowGuideChange: (Boolean) -> Unit,
    onAutoSaveHistoryChange: (Boolean) -> Unit,
    onSpellCorrectionChange: (Boolean) -> Unit
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Cream
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recognition Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsToggle(
                icon = Icons.Default.RecordVoiceOver,
                title = "Auto-speak after recognition",
                subtitle = "Automatically read detected text aloud",
                checked = autoSpeak,
                onCheckedChange = onAutoSpeakChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggle(
                icon = Icons.Default.ViewInAr,
                title = "Show camera guide",
                subtitle = "Display alignment overlay on camera",
                checked = showGuide,
                onCheckedChange = onShowGuideChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggle(
                icon = Icons.Default.Save,
                title = "Auto-save to history",
                subtitle = "Save all recognition results",
                checked = autoSaveHistory,
                onCheckedChange = onAutoSaveHistoryChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggle(
                icon = Icons.Default.Spellcheck,
                title = "Spell correction",
                subtitle = "Auto-correct recognized text",
                checked = spellCorrection,
                onCheckedChange = onSpellCorrectionChange
            )
        }
    }
}

@Composable
private fun SettingsToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Black.copy(alpha = 0.7f),
            modifier = Modifier.padding(end = 12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Black
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Black.copy(alpha = 0.6f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Black,
                checkedTrackColor = Yellow,
                uncheckedThumbColor = Black.copy(alpha = 0.5f),
                uncheckedTrackColor = Black.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun AppInfoCard(
    isModelReady: Boolean,
    versionName: String
) {
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Cream
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "App Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoRow(label = "App Version", value = versionName)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Model", value = "YOLOv8-nano (320x320)")
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Classes", value = "26 (a-z)")
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                label = "Model Status",
                value = if (isModelReady) "Loaded" else "Not Loaded"
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Black.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Black
        )
    }
}
