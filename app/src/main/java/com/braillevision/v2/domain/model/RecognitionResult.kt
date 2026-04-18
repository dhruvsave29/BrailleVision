package com.braillevision.v2.domain.model

data class RecognitionResult(
    val originalText: String,
    val correctedText: String,
    val confidence: Float,
    val detections: List<Detection>,
    val processingTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    val characterCount: Int
        get() = detections.size
    
    val hasResults: Boolean
        get() = detections.isNotEmpty()
    
    companion object {
        val EMPTY = RecognitionResult(
            originalText = "",
            correctedText = "",
            confidence = 0f,
            detections = emptyList(),
            processingTimeMs = 0L
        )
    }
}
