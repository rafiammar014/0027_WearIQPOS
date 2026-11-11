package com.example.finalproject.ui.screens.pos

import androidx.lifecycle.ViewModel
import com.example.finalproject.data.model.TransactionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PosUiState(
    val isLoading: Boolean = false,
    val cartItems: List<TransactionItem> = emptyList(),
    val totalAmount: Double = 0.0
)

class PosViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()
}
