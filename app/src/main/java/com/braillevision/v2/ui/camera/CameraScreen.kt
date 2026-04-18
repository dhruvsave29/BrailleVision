package com.braillevision.v2.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.braillevision.v2.R
import com.braillevision.v2.ui.components.NeoTextButton
import com.braillevision.v2.ui.theme.Black
import com.braillevision.v2.ui.theme.OffWhite
import com.braillevision.v2.ui.theme.Yellow
import java.io.File
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onNavigateToResult: (Uri) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { 
        ImageCapture.Builder()
            .setFlashMode(uiState.flashMode)
            .build() 
    }
    
    var hasRequestedPermission = remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
        if (!isGranted) {
            Toast.makeText(
                context,
                context.getString(R.string.camera_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onNavigateToResult(it) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.checkCameraPermission()
    }
    
    LaunchedEffect(uiState.hasCameraPermission, hasRequestedPermission.value) {
        if (!uiState.hasCameraPermission && !hasRequestedPermission.value) {
            hasRequestedPermission.value = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    LaunchedEffect(uiState.capturedImageUri) {
        uiState.capturedImageUri?.let { uri ->
            onNavigateToResult(uri)
            viewModel.clearCapturedImage()
        }
    }
    
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = stringResource(R.string.history)
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.hasCameraPermission) {
                CameraPreviewContent(
                    modifier = Modifier.fillMaxSize(),
                    lifecycleOwner = lifecycleOwner,
                    imageCapture = imageCapture,
                    cameraExecutor = cameraExecutor,
                    flashMode = uiState.flashMode,
                    showGuide = userPrefs.showGuide,
                    isCapturing = uiState.isCapturing,
                    onCapture = {
                        viewModel.startCapture()
                        captureImage(
                            imageCapture = imageCapture,
                            executor = cameraExecutor,
                            onImageCaptured = { file -> 
                                viewModel.onImageCaptured(file)
                            },
                            onError = { message -> 
                                viewModel.onCaptureError(message)
                            }
                        )
                    },
                    onToggleFlash = { viewModel.toggleFlash() },
                    onOpenGallery = { galleryLauncher.launch("image/*") }
                )
            } else {
                PermissionRequestContent(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    imageCapture: ImageCapture,
    cameraExecutor: java.util.concurrent.Executor,
    flashMode: Int,
    showGuide: Boolean,
    isCapturing: Boolean,
    onCapture: () -> Unit,
    onToggleFlash: () -> Unit,
    onOpenGallery: () -> Unit
) {
    val context = LocalContext.current
    val previewView = remember { androidx.camera.view.PreviewView(context) }
    
    LaunchedEffect(flashMode) {
        imageCapture.flashMode = flashMode
    }
    
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        
        val preview = Preview.Builder()
            .build()
            .also { it.surfaceProvider = previewView.surfaceProvider }
        
        val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        if (showGuide) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(280.dp)
                    .border(3.dp, Yellow)
            )
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleFlash,
                    modifier = Modifier
                        .size(56.dp)
                        .background(OffWhite, CircleShape)
                        .border(3.dp, Black, CircleShape)
                ) {
                    Icon(
                        imageVector = when (flashMode) {
                            ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                            ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                            else -> Icons.Default.FlashOff
                        },
                        contentDescription = stringResource(R.string.flash),
                        tint = Black
                    )
                }
                
                IconButton(
                    onClick = onCapture,
                    enabled = !isCapturing,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Yellow, CircleShape)
                        .border(3.dp, Black, CircleShape)
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Black,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.capture),
                            tint = Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = onOpenGallery,
                    modifier = Modifier
                        .size(56.dp)
                        .background(OffWhite, CircleShape)
                        .border(3.dp, Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = stringResource(R.string.gallery),
                        tint = Black
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "BrailleVision needs camera access to capture braille text images.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        NeoTextButton(
            text = "Grant Permission",
            onClick = onRequestPermission,
            backgroundColor = Yellow
        )
    }
}

private fun captureImage(
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onImageCaptured: (File) -> Unit,
    onError: (String) -> Unit
) {
    val outputFile = File.createTempFile(
        "braille_${System.currentTimeMillis()}",
        ".jpg"
    )
    
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(outputFile)
            }
            
            override fun onError(exception: ImageCaptureException) {
                onError(exception.message ?: "Failed to capture image")
            }
        }
    )
}
