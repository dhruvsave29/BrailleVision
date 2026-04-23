package com.braillevision.v2.ui.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.braillevision.v2.R
import com.braillevision.v2.ui.components.NeoCard
import com.braillevision.v2.ui.components.NeoTextButton
import com.braillevision.v2.ui.theme.Black
import com.braillevision.v2.ui.theme.Cream
import com.braillevision.v2.ui.theme.Green
import com.braillevision.v2.ui.theme.OffWhite
import com.braillevision.v2.ui.theme.Success
import com.braillevision.v2.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    imageUri: Uri?,
    onNavigateBack: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    
    LaunchedEffect(imageUri) {
        imageUri?.let { viewModel.processImage(it) }
    }
    
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.recognized_text),
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                LoadingContent()
            } else if (uiState.error != null) {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = { imageUri?.let { viewModel.processImage(it) } }
                )
            } else if (uiState.hasResults) {
                ResultContent(
                    imageUri = imageUri,
                    originalText = uiState.originalText,
                    correctedText = uiState.correctedText,
                    confidence = uiState.confidence,
                    characterCount = uiState.characterCount,
                    isSpeaking = isSpeaking,
                    onSpeak = { viewModel.speakText() },
                    onCopy = {
                        val clip = ClipData.newPlainText("braille_text", uiState.correctedText)
                        clipboardManager.setPrimaryClip(clip)
                    },
                    onSave = { viewModel.saveToHistory(imageUri) },
                    onRetake = onNavigateBack
                )
            } else {
                NoResultsContent(onRetake = onNavigateBack)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Black,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.processing),
            style = MaterialTheme.typography.titleMedium,
            color = Black
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        NeoTextButton(
            text = "Retry",
            onClick = onRetry,
            backgroundColor = Yellow
        )
    }
}

@Composable
private fun NoResultsContent(
    onRetake: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_braille_detected),
            style = MaterialTheme.typography.titleLarge,
            color = Black
        )
        Spacer(modifier = Modifier.height(24.dp))
        NeoTextButton(
            text = stringResource(R.string.retake),
            onClick = onRetake,
            backgroundColor = Yellow
        )
    }
}

@Composable
private fun ResultContent(
    imageUri: Uri?,
    originalText: String,
    correctedText: String,
    confidence: Float,
    characterCount: Int,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    onRetake: () -> Unit
) {
    val context = LocalContext.current
    
    val bitmap = remember(imageUri) {
        imageUri?.let { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    bitmap?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .border(3.dp, Black)
        ) {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Captured image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Cream
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"$correctedText\"",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Black,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = stringResource(R.string.confidence),
                    value = "${(confidence * 100).toInt()}%"
                )
                StatItem(
                    label = stringResource(R.string.characters),
                    value = characterCount.toString()
                )
            }
        }
    }
    
    if (originalText != correctedText) {
        Spacer(modifier = Modifier.height(12.dp))
        
NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Cream
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.original),
                        style = MaterialTheme.typography.labelMedium,
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\"$originalText\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.corrected),
                        style = MaterialTheme.typography.labelMedium,
                        color = Success,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\"$correctedText\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success
                    )
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NeoTextButton(
            text = if (isSpeaking) "Speaking..." else stringResource(R.string.speak),
            onClick = onSpeak,
            backgroundColor = if (isSpeaking) Green else Yellow,
            modifier = Modifier.weight(1f)
        )
        NeoTextButton(
            text = stringResource(R.string.copy),
            onClick = onCopy,
            modifier = Modifier.weight(1f)
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NeoTextButton(
            text = stringResource(R.string.save),
            onClick = onSave,
            modifier = Modifier.weight(1f)
        )
        NeoTextButton(
            text = stringResource(R.string.retake),
            onClick = onRetake,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Black,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Black.copy(alpha = 0.6f)
        )
    }
}
