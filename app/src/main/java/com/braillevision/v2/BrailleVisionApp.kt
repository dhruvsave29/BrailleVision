package com.braillevision.v2

import android.app.Application
import android.util.Log
import com.braillevision.v2.data.tflite.YoloDetector
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BrailleVisionApp : Application() {

    @Inject
    lateinit var yoloDetector: YoloDetector

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        applicationScope.launch {
            try {
                val loaded = yoloDetector.preloadModel()
                if (loaded) {
                    Log.d("BrailleVisionApp", "ML Model loaded successfully")
                } else {
                    Log.e("BrailleVisionApp", "Failed to load ML Model")
                }
            } catch (e: Exception) {
                Log.e("BrailleVisionApp", "Error loading ML Model: ${e.message}", e)
            }
        }
    }
}
