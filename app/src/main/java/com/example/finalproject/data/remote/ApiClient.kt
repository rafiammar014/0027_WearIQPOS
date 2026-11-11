package com.example.finalproject.data.remote

import android.content.Context
import com.example.finalproject.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton / Factory untuk menginisialisasi Retrofit Client beserta:
 * - AuthInterceptor (penyisipan JWT Access Token)
 * - TokenAuthenticator (auto-refresh Access Token menggunakan Refresh Token saat 401)
 * - HttpLoggingInterceptor (monitoring request & response API)
 */
object ApiClient {

    // Ganti dengan Base URL REST API backend WearIQ POS Anda (gunakan 10.0.2.2 untuk localhost di Emulator)
    const val BASE_URL = "http://10.0.2.2:8000/api/"

    @Volatile
    private var apiServiceInstance: ApiService? = null

    @Volatile
    private var tokenManagerInstance: TokenManager? = null

    fun getTokenManager(context: Context): TokenManager {
        return tokenManagerInstance ?: synchronized(this) {
            tokenManagerInstance ?: TokenManager(context.applicationContext).also {
                tokenManagerInstance = it
            }
        }
    }

    fun getApiService(context: Context): ApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: buildApiService(getTokenManager(context)).also {
                apiServiceInstance = it
            }
        }
    }

    private fun buildApiService(tokenManager: TokenManager): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Client khusus untuk proses refresh token (tanpa Authenticator agar tidak rekursif)
        val refreshOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val refreshApiService: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(refreshOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        val authInterceptor = AuthInterceptor(tokenManager)
        val tokenAuthenticator = TokenAuthenticator(
            tokenManager = tokenManager,
            refreshApiProvider = { refreshApiService }
        )

        val mainOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
