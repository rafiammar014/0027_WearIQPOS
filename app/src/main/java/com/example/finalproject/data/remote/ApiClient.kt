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
 * - HostFallbackInterceptor (otomatis mencoba 10.0.2.2, 127.0.0.1 via adb reverse, atau LAN IP jika salah satu diblokir firewall)
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

        // Client khusus untuk proses refresh token (tanpa Authenticator agar tidak rekursif)
        val refreshOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(fallbackInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
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
            .connectTimeout(5, TimeUnit.SECONDS)
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
 * Interceptor pintar yang otomatis beralih host bila koneksi emulator ke host mengalami timeout
 * karena proteksi Windows Firewall atau perbedaan konfigurasi adapter jaringan emulator.
 */
class HostFallbackInterceptor : Interceptor {

    companion object {
        private val CANDIDATE_HOSTS = listOf("10.0.2.2", "127.0.0.1", "192.168.104.158")

        @Volatile
        var activeHost: String = "10.0.2.2"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val hostsToTry = linkedSetOf(activeHost).apply {
            addAll(CANDIDATE_HOSTS)
        }

        var lastException: IOException? = null

        for (host in hostsToTry) {
            val newUrl = originalUrl.newBuilder().host(host).build()
            val newRequest = originalRequest.newBuilder().url(newUrl).build()

            try {
                val response = chain.proceed(newRequest)
                activeHost = host
                return response
            } catch (e: IOException) {
                lastException = e
            }
        }

        throw lastException ?: IOException("Tidak dapat terhubung ke server pada host manapun: $hostsToTry")
    }
}
