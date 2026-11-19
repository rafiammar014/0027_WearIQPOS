package com.example.finalproject.data.remote

import android.content.Context
import com.example.finalproject.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Singleton / Factory untuk menginisialisasi Retrofit Client beserta:
 * - HostFallbackInterceptor (memprioritaskan 10.0.2.2 untuk emulator Android)
 * - AuthInterceptor (penyisipan JWT Access Token)
 * - TokenAuthenticator (auto-refresh Access Token menggunakan Refresh Token saat 401)
 * - HttpLoggingInterceptor (monitoring request & response API)
 */
object ApiClient {

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

        val fallbackInterceptor = HostFallbackInterceptor()

        // Client khusus untuk proses refresh token
        val refreshOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(fallbackInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
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
            .addInterceptor(fallbackInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(mainOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

/**
 * HostFallbackInterceptor:
 * Selalu mencoba 10.0.2.2 terlebih dahulu (karena 10.0.2.2 adalah IP gateway loopback emulator Android).
 * Jika gagal, mencoba 127.0.0.1 (via adb reverse), lalu LAN IP jika ada perangkat fisik.
 */
class HostFallbackInterceptor : Interceptor {

    companion object {
        private val CANDIDATE_HOSTS = listOf("10.0.2.2", "127.0.0.1", "192.168.104.158")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        var lastException: IOException? = null

        for (host in CANDIDATE_HOSTS) {
            val newUrl = originalUrl.newBuilder().host(host).build()
            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            try {
                return chain.proceed(newRequest)
            } catch (e: IOException) {
                lastException = e
            }
        }

        throw lastException ?: IOException("Tidak dapat terhubung ke server backend di host: $CANDIDATE_HOSTS")
    }
}
