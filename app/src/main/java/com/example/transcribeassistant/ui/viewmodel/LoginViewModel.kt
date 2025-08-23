package com.example.transcribeassistant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.data.dto.JwtAuthResponse
import com.example.transcribeassistant.data.network.AuthApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Does the login call and saves tokens
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val jwtManager: JwtManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Login with Google credential and save tokens to datastore
     */
    fun loginWithGoogle(googleCredential: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                Log.d("LoginViewModel", "Calling loginWithGoogle with credential ending: ${googleCredential.takeLast(8)}")

                Log.d("LoginViewModel", "Sending login payload: ${mapOf("idToken" to googleCredential).toString().take(200)}")

                // Call API to get tokens back from Google OAuth endpoint and refresh token
                val response: JwtAuthResponse = authApi.loginWithGoogle(mapOf("idToken" to googleCredential))
                // Save tokens
                jwtManager.saveTokens(response.accessToken, response.refreshToken)
                Log.d("LoginViewModel", "Saved access (last 8): ${response.accessToken.takeLast(8)}, refresh (last 8): ${response.refreshToken.takeLast(8)}")
                _uiState.value = LoginUiState.Success(response.accessToken.takeLast(8))
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed", e)
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setError(message: String) {
        _uiState.value = LoginUiState.Error(message)
    }

    fun resetToIdle() {
        _uiState.value = LoginUiState.Idle
    }
}