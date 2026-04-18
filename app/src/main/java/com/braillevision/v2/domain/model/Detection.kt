package com.braillevision.v2.domain.model

import android.graphics.RectF

data class Detection(
    val boundingBox: RectF,
    val confidence: Float,
    val classIndex: Int,
    val character: String,
    val centerX: Float,
    val centerY: Float
) {
    companion object {
        val EMPTY = Detection(
            boundingBox = RectF(),
            confidence = 0f,
            classIndex = -1,
            character = "",
            centerX = 0f,
            centerY = 0f
        )
    }
}
