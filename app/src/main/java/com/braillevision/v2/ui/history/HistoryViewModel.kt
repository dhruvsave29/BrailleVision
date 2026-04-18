package com.braillevision.v2.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braillevision.v2.data.local.HistoryDao
import com.braillevision.v2.data.local.HistoryEntity
import com.braillevision.v2.domain.usecase.SpeakTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    private val speakTextUseCase: SpeakTextUseCase
) : ViewModel() {

    val history = historyDao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun speakText(text: String) {
        speakTextUseCase.speak(text)
    }

    fun deleteItem(item: HistoryEntity) {
        viewModelScope.launch {
            historyDao.delete(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyDao.deleteAll()
        }
    }
}
