package com.example.transcribeassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.data.cache.AuthManager
import com.example.transcribeassistant.data.dto.GoogleAuthRequest
import com.example.transcribeassistant.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authManager: AuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkIfUserIsAuthenticated()
    }

    private fun checkIfUserIsAuthenticated() {
        val token = authManager.getToken()
        if (token != null) {
            _authState.value = AuthState.SignedIn(token)
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val request = GoogleAuthRequest(idToken)
            val result = authRepository.authenticateWithGoogle(request)
            result.onSuccess { response ->
                authManager.saveToken(response.accessToken)
                _authState.value = AuthState.SignedIn(response.accessToken)
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "An unknown error occurred")
            }
        }
    }

    fun signOut() {
        authManager.clearToken()
        _authState.value = AuthState.SignedOut
    }
}

sealed class AuthState {
    object SignedOut : AuthState()
    object Loading : AuthState()
    data class SignedIn(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
} 