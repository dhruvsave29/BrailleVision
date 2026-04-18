package com.braillevision.v2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val correctedText: String,
    val confidence: Float,
    val characterCount: Int,
    val imageUri: String,
    val processingTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
