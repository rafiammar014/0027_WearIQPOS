package com.example.finalproject.ui.screens.ai_insight

import androidx.lifecycle.ViewModel
import com.example.finalproject.data.model.AiInsight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AiInsightUiState(
    val isLoading: Boolean = false,
    val insights: List<AiInsight> = emptyList()
)

class AiInsightViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AiInsightUiState())
    val uiState: StateFlow<AiInsightUiState> = _uiState.asStateFlow()
}
