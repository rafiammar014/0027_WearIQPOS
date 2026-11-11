package com.example.finalproject.ui.screens.catalog

import androidx.lifecycle.ViewModel
import com.example.finalproject.data.model.Category
import com.example.finalproject.data.model.ProductVariant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CatalogUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val variants: List<ProductVariant> = emptyList(),
    val errorMessage: String? = null
)

class CatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()
}
