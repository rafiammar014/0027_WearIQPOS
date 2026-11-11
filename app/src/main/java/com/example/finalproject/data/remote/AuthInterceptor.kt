package com.example.finalproject.data.remote

import com.example.finalproject.data.model.RefreshTokenRequest
import com.example.finalproject.utils.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Interceptor untuk menyisipkan Access Token (JWT) ke setiap request yang membutuhkan autentikasi.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Lewati penambahan Bearer token jika endpoint ditandai "No-Authentication: true"
        if (originalRequest.header("No-Authentication") == "true") {
            val cleanRequest = originalRequest.newBuilder()
                .removeHeader("No-Authentication")
                .build()
            return chain.proceed(cleanRequest)
        }

        val accessToken = tokenManager.getAccessTokenBlocking()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")

        if (!accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}

/**
 * OkHttp Authenticator untuk menangani HTTP 401 Unauthorized secara otomatis:
 * 1. Mengambil refresh_token dari TokenManager (DataStore).
 * 2. Memanggil endpoint refresh token secara thread-safe (synchronized).
 * 3. Menyimpan access_token (dan refresh_token baru) ke DataStore.
 * 4. Mengulangi request awal yang gagal dengan header Authorization yang baru.
 */
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApiProvider: () -> ApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Cegah infinite loop jika retry sudah gagal >= 2 kali
        if (responseCount(response) >= 2) {
            return null
        }

        // Jangan refresh jika yang gagal adalah endpoint login atau refresh itu sendiri
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/login") || path.endsWith("/auth/refresh")) {
            return null
        }

        synchronized(this) {
            val currentAccessToken = tokenManager.getAccessTokenBlocking()
            val requestAuthHeader = response.request.header("Authorization")
            val requestAccessToken = requestAuthHeader?.removePrefix("Bearer ")?.trim()

            // Jika token di DataStore sudah berubah (di-refresh oleh thread lain), langsung retry dengan token baru
            if (!currentAccessToken.isNullOrBlank() && currentAccessToken != requestAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshTokenBlocking()
            if (refreshToken.isNullOrBlank()) {
                tokenManager.clearSessionBlocking()
                return null
            }

            return runBlocking {
                try {
                    val refreshResponse = refreshApiProvider().refreshToken(
                        RefreshTokenRequest(refreshToken = refreshToken)
                    )

                    val body = refreshResponse.body()
                    if (refreshResponse.isSuccessful && body != null && body.accessToken.isNotBlank()) {
                        // Simpan token baru ke DataStore
                        tokenManager.updateTokens(
                            accessToken = body.accessToken,
                            refreshToken = body.refreshToken
                        )

                        // Ulangi request semula dengan Access Token baru
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${body.accessToken}")
                            .build()
                    } else {
                        // Refresh token sudah tidak valid / kedaluwarsa -> hapus sesi
                        tokenManager.clearSession()
                        null
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
