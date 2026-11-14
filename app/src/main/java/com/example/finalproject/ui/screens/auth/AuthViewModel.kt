package com.example.finalproject.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiClient
import com.example.finalproject.data.repository.WearIqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userRole: String? = null,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: WearIqRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.login(email, password)
                .onSuccess { response ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        userRole = response.user.role
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
        role: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.register(fullName, username, email, password, role)
                .onSuccess { response ->
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isLoggedIn = true,
                        userRole = response.user.role
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState()
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val apiService = ApiClient.getApiService(context)
                val tokenManager = ApiClient.getTokenManager(context)
                val repository = WearIqRepository(apiService, tokenManager)
                return AuthViewModel(repository) as T
            }
        }
    }
}
