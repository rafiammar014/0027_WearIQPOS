package com.example.finalproject.ui.screens.restock

import androidx.lifecycle.ViewModel
import com.example.finalproject.data.model.ProductVariant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RestockUiState(
    val isLoading: Boolean = false,
    val lowStockVariants: List<ProductVariant> = emptyList()
)

class RestockViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RestockUiState())
    val uiState: StateFlow<RestockUiState> = _uiState.asStateFlow()
}
