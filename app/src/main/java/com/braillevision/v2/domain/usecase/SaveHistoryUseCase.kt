package com.braillevision.v2.domain.usecase

import com.braillevision.v2.data.local.HistoryDao
import com.braillevision.v2.data.local.HistoryEntity
import com.braillevision.v2.domain.model.RecognitionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveHistoryUseCase @Inject constructor(
    private val historyDao: HistoryDao
) {
    suspend operator fun invoke(
        result: RecognitionResult,
        imageUri: String
    ): Long {
        val entity = HistoryEntity(
            originalText = result.originalText,
            correctedText = result.correctedText,
            confidence = result.confidence,
            characterCount = result.characterCount,
            imageUri = imageUri,
            processingTimeMs = result.processingTimeMs,
            timestamp = result.timestamp
        )
        return historyDao.insert(entity)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }
}
