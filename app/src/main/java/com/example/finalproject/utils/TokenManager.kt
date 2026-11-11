package com.example.finalproject.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weariq_auth_prefs")

/**
 * TokenManager menggunakan DataStore Preferences khusus untuk menyimpan:
 * - access_token (JWT)
 * - refresh_token
 * - role pengguna (misal: "ADMIN", "CASHIER", "OWNER")
 *
 * Sesuai aturan arsitektur WearIQ POS: TIDAK menggunakan Room Database.
 */
class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val userRoleFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_ROLE_KEY]
    }

    /**
     * Menyimpan sesi autentikasi lengkap (access_token, refresh_token, dan role).
     */
    suspend fun saveSession(accessToken: String, refreshToken: String, role: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ROLE_KEY] = role
        }
    }

    /**
     * Memperbarui access_token dan refresh_token baru hasil auto-refresh.
     */
    suspend fun updateTokens(accessToken: String, refreshToken: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            if (!refreshToken.isNullOrBlank()) {
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
        }
    }

    /**
     * Mengambil access_token secara sinkron (blocking) untuk OkHttp Interceptor/Authenticator.
     */
    fun getAccessTokenBlocking(): String? = runBlocking {
        accessTokenFlow.firstOrNull()
    }

    /**
     * Mengambil refresh_token secara sinkron (blocking) untuk OkHttp Authenticator.
     */
    fun getRefreshTokenBlocking(): String? = runBlocking {
        refreshTokenFlow.firstOrNull()
    }

    /**
     * Menghapus seluruh sesi saat logout atau ketika refresh_token sudah kedaluwarsa/invalid.
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ROLE_KEY)
        }
    }

    fun clearSessionBlocking() = runBlocking {
        clearSession()
    }
}
