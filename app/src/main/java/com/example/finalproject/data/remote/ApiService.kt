package com.example.finalproject.data.remote

import com.example.finalproject.data.model.AiInsight
import com.example.finalproject.data.model.Category
import com.example.finalproject.data.model.LoginRequest
import com.example.finalproject.data.model.LoginResponse
import com.example.finalproject.data.model.ProductVariant
import com.example.finalproject.data.model.RefreshTokenRequest
import com.example.finalproject.data.model.RefreshTokenResponse
import com.example.finalproject.data.model.Transaction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit Interface untuk seluruh endpoint REST API WearIQ POS.
 */
interface ApiService {

    // ==================== AUTH ====================
    @Headers("No-Authentication: true")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Headers("No-Authentication: true")
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    // ==================== CATALOG & CATEGORIES ====================
    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("products/variants")
    suspend fun getProductVariants(): Response<List<ProductVariant>>

    // ==================== POS & TRANSACTIONS ====================
    @GET("transactions")
    suspend fun getTransactions(): Response<List<Transaction>>

    @POST("transactions")
    suspend fun createTransaction(@Body transaction: Transaction): Response<Transaction>

    // ==================== AI INSIGHTS ====================
    @GET("ai-insights")
    suspend fun getAiInsights(): Response<List<AiInsight>>
}
