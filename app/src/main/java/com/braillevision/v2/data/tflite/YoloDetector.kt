package com.braillevision.v2.data.tflite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import com.braillevision.v2.domain.model.Detection
import com.braillevision.v2.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YoloDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    
    private val inputSize = Constants.INPUT_SIZE
    private val numClasses = Constants.NUM_CLASSES
    private val confidenceThreshold = Constants.CONFIDENCE_THRESHOLD
    private val nmsThreshold = Constants.NMS_THRESHOLD

    @Volatile
    private var isModelLoaded = false
    
    private var loadError: String? = null
    
    private val lock = Any()

    fun preloadModel(): Boolean {
        return loadModel()
    }

    private fun ensureModelLoaded() {
        if (!isModelLoaded) {
            synchronized(lock) {
                if (!isModelLoaded) {
                    loadModel()
                }
            }
        }
    }

    private fun loadModel(): Boolean {
        try {
            Log.d("YoloDetector", "Starting model load from assets...")
            
            // Check if model file exists in assets
            val assetFiles = context.assets.list("") ?: emptyArray()
            Log.d("YoloDetector", "Assets folder contains: ${assetFiles.joinToString(", ")}")
            
            val modelFile = Constants.MODEL_FILENAME
            Log.d("YoloDetector", "Looking for model: $modelFile")
            
            if (!assetFiles.contains(modelFile)) {
                loadError = "Model file not found in assets: $modelFile"
                Log.e("YoloDetector", loadError!!)
                return false
            }
            
            val modelBuffer = loadModelFile()
            Log.d("YoloDetector", "Model file loaded, size: ${modelBuffer.capacity()} bytes")
            
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            
            interpreter = Interpreter(modelBuffer, options)
            
            // Verify model input/output shapes
            val inputShape = interpreter?.getInputTensor(0)?.shape()
            val outputShape = interpreter?.getOutputTensor(0)?.shape()
            Log.d("YoloDetector", "Model input shape: ${inputShape?.contentToString()}")
            Log.d("YoloDetector", "Model output shape: ${outputShape?.contentToString()}")
            
            isModelLoaded = true
            loadError = null
            Log.d("YoloDetector", "Model loaded successfully!")
            return true
        } catch (e: Exception) {
            loadError = "Failed to load model: ${e.message}"
            Log.e("YoloDetector", "Error loading model", e)
            return false
        }
    }

    fun getLoadError(): String? = loadError

    fun detect(bitmap: Bitmap): List<Detection> {
        ensureModelLoaded()
        
        if (!isModelLoaded) {
            Log.e("YoloDetector", "Model not loaded! Error: $loadError")
            return emptyList()
        }
        
        try {
            val inputBitmap = preprocessBitmap(bitmap)
            val inputBuffer = bitmapToByteBuffer(inputBitmap)
            
            val outputShape = interpreter?.getOutputTensor(0)?.shape()
            val numFeatures = outputShape?.get(1) ?: 30
            val numBoxes = outputShape?.get(2) ?: 2100
            
            val output = Array(1) { Array(numFeatures) { FloatArray(numBoxes) } }
            
            interpreter?.run(inputBuffer, output)
            
            val detections = postprocess(output[0], numFeatures, numBoxes, bitmap.width, bitmap.height)
            val nmsDetections = applyNMS(detections)
            
            Log.d("YoloDetector", "Detected ${nmsDetections.size} characters")
            return nmsDetections
        } catch (e: Exception) {
            Log.e("YoloDetector", "Detection error", e)
            return emptyList()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(Constants.MODEL_FILENAME)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): java.nio.ByteBuffer {
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(java.nio.ByteOrder.nativeOrder())
        
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        for (pixel in intValues) {
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixel and 0xFF) / 255.0f)
        }
        
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun postprocess(
        output: Array<FloatArray>,
        numFeatures: Int,
        numBoxes: Int,
        originalWidth: Int,
        originalHeight: Int
    ): List<Detection> {
        val detections = mutableListOf<Detection>()
        
        val scaleX = originalWidth.toFloat() / inputSize
        val scaleY = originalHeight.toFloat() / inputSize
        
        for (i in 0 until numBoxes) {
            val x = output[0][i]
            val y = output[1][i]
            val w = output[2][i]
            val h = output[3][i]
            
            var maxClassScore = Float.MIN_VALUE
            var classIndex = 0
            
            for (c in 0 until numClasses) {
                val score = output[4 + c][i]
                if (score > maxClassScore) {
                    maxClassScore = score
                    classIndex = c
                }
            }
            
            if (maxClassScore < confidenceThreshold) continue
            
            val left = (x - w / 2) * scaleX
            val top = (y - h / 2) * scaleY
            val right = (x + w / 2) * scaleX
            val bottom = (y + h / 2) * scaleY
            
            detections.add(
                Detection(
                    boundingBox = RectF(left, top, right, bottom),
                    confidence = maxClassScore,
                    classIndex = classIndex,
                    character = Constants.getClassCharacter(classIndex).toString(),
                    centerX = x * scaleX,
                    centerY = y * scaleY
                )
            )
        }
        
        return detections
    }

    private fun applyNMS(detections: List<Detection>): List<Detection> {
        if (detections.isEmpty()) return emptyList()
        
        val sortedDetections = detections.sortedByDescending { it.confidence }
        val selected = mutableListOf<Detection>()
        
        for (detection in sortedDetections) {
            var shouldAdd = true
            for (selectedDetection in selected) {
                val iou = calculateIoU(detection.boundingBox, selectedDetection.boundingBox)
                if (iou > nmsThreshold) {
                    shouldAdd = false
                    break
                }
            }
            if (shouldAdd) {
                selected.add(detection)
            }
        }
        
        return sortDetectionsForReading(selected)
    }

    private fun calculateIoU(box1: RectF, box2: RectF): Float {
        val intersectionLeft = maxOf(box1.left, box2.left)
        val intersectionTop = maxOf(box1.top, box2.top)
        val intersectionRight = minOf(box1.right, box2.right)
        val intersectionBottom = minOf(box1.bottom, box2.bottom)
        
        if (intersectionRight < intersectionLeft || intersectionBottom < intersectionTop) {
            return 0f
        }
        
        val intersectionArea = (intersectionRight - intersectionLeft) * (intersectionBottom - intersectionTop)
        val box1Area = box1.width() * box1.height()
        val box2Area = box2.width() * box2.height()
        val unionArea = box1Area + box2Area - intersectionArea
        
        return if (unionArea > 0) intersectionArea / unionArea else 0f
    }

    private fun sortDetectionsForReading(detections: List<Detection>): List<Detection> {
        if (detections.isEmpty()) return emptyList()
        
        val avgHeight = detections.map { it.boundingBox.height() }.average().toFloat()
        val rowThreshold = avgHeight * 0.5f
        
        val rows = mutableListOf<MutableList<Detection>>()
        val sortedByY = detections.sortedBy { it.centerY }
        
        for (detection in sortedByY) {
            var addedToRow = false
            for (row in rows) {
                val rowCenterY = row.map { it.centerY }.average().toFloat()
                if (kotlin.math.abs(detection.centerY - rowCenterY) < rowThreshold) {
                    row.add(detection)
                    addedToRow = true
                    break
                }
            }
            if (!addedToRow) {
                rows.add(mutableListOf(detection))
            }
        }
        
        return rows.flatMap { row -> row.sortedBy { it.centerX } }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
        isModelLoaded = false
    }

    fun isReady(): Boolean = isModelLoaded
}
