package com.example.finalproject.data.repository

import com.example.finalproject.data.model.AiInsight
import com.example.finalproject.data.model.Category
import com.example.finalproject.data.model.LoginRequest
import com.example.finalproject.data.model.LoginResponse
import com.example.finalproject.data.model.ProductVariant
import com.example.finalproject.data.model.Transaction
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.utils.TokenManager
import kotlinx.coroutines.flow.Flow

/**
 * Repository utama penghubung antara ApiService (REST API) dan ViewModel.
 * Seluruh data bisnis diambil dari REST API (tanpa Room Database).
 */
class WearIqRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    val accessTokenFlow: Flow<String?> = tokenManager.accessTokenFlow
    val userRoleFlow: Flow<String?> = tokenManager.userRoleFlow

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email = email, password = password))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                tokenManager.saveSession(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    role = body.user.role
                )
                Result.success(body)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string()) ?: response.message()
                Result.failure(Exception(errorMsg.ifBlank { "Email atau kata sandi tidak valid." }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server backend: ${e.localizedMessage}"))
        }
    }

    suspend fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
        role: String
    ): Result<LoginResponse> {
        return try {
            val request = com.example.finalproject.data.model.RegisterRequest(
                fullName = fullName,
                username = username,
                email = email,
                password = password,
                role = role
            )
            val response = apiService.register(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                tokenManager.saveSession(
                    accessToken = body.accessToken,
                    refreshToken = body.refreshToken,
                    role = body.user.role
                )
                Result.success(body)
            } else {
                val errorMsg = extractErrorMessage(response.errorBody()?.string()) ?: response.message()
                Result.failure(Exception(errorMsg.ifBlank { "Registrasi gagal." }))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server backend: ${e.localizedMessage}"))
        }
    }

    private fun extractErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val jsonObject = com.google.gson.JsonParser.parseString(json).asJsonObject
            jsonObject.get("message")?.asString
        } catch (_: Exception) {
            json
        }
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (_: Exception) {
            // Tetap bersihkan token lokal meskipun jaringan gagal
        } finally {
            tokenManager.clearSession()
        }
    }

    suspend fun getCategories(): Result<List<Category>> = safeApiCall {
        apiService.getCategories()
    }

    suspend fun getProductVariants(): Result<List<ProductVariant>> = safeApiCall {
        apiService.getProductVariants()
    }

    suspend fun getTransactions(): Result<List<Transaction>> = safeApiCall {
        apiService.getTransactions()
    }

    suspend fun createTransaction(transaction: Transaction): Result<Transaction> = safeApiCall {
        apiService.createTransaction(transaction)
    }

    suspend fun getAiInsights(): Result<List<AiInsight>> = safeApiCall {
        apiService.getAiInsights()
    }

    private inline fun <T> safeApiCall(call: () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("API Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
