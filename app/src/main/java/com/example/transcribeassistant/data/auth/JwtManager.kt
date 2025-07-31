package com.example.transcribeassistant.data.auth

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.transcribeassistant.data.extensions.dataStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first


/**
 * Handles saving & retrieving JWTs from DataStore.
 */
class JwtManager(private val context: Context) {

    companion object {
        private val ACCESS_KEY = stringPreferencesKey("access_token")
        private val REFRESH_KEY = stringPreferencesKey("refresh_token")
    }

    /**
     * Save both access + refresh tokens.
     */
    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_KEY]  = access
            prefs[REFRESH_KEY] = refresh
        }
    }

    /**
     * Returns the current access token, or null if none.
     */
    fun getAccessToken(): String? = runBlocking {
        context.dataStore.data.first()[ACCESS_KEY]
    }

    /**
     * Returns the current refresh token, or null if none.
     */
    fun getRefreshToken(): String? = runBlocking {
        context.dataStore.data.first()[REFRESH_KEY]
    }

    /**
     * Clears both tokens (e.g. on logout).
     */
    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}