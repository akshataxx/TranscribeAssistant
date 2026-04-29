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
        private val PROFILE_NAME_KEY = stringPreferencesKey("profile_name")
        private val PROFILE_EMAIL_KEY = stringPreferencesKey("profile_email")
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
     * Save user profile info.
     */
    suspend fun saveProfile(name: String?, email: String?) {
        context.dataStore.edit { prefs ->
            if (name != null) prefs[PROFILE_NAME_KEY] = name
            if (email != null) prefs[PROFILE_EMAIL_KEY] = email
        }
    }

    /**
     * Returns the stored profile name, or null.
     */
    fun getProfileName(): String? = runBlocking {
        context.dataStore.data.first()[PROFILE_NAME_KEY]
    }

    /**
     * Returns the stored profile email, or null.
     */
    fun getProfileEmail(): String? = runBlocking {
        context.dataStore.data.first()[PROFILE_EMAIL_KEY]
    }

    /**
     * Clears only auth tokens and profile (e.g. on logout).
     * Does NOT clear other DataStore keys like onboarding flags.
     */
    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_KEY)
            prefs.remove(REFRESH_KEY)
            prefs.remove(PROFILE_NAME_KEY)
            prefs.remove(PROFILE_EMAIL_KEY)
        }
    }
}