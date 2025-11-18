package com.example.transcribeassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.transcribeassistant.data.auth.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

/**
 * ViewModel for managing authentication state across the app
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authStateManager: AuthStateManager
) : ViewModel() {

    val authenticationExpired: SharedFlow<Unit> = authStateManager.authenticationExpired

    fun signalAuthenticationExpired() {
        authStateManager.signalAuthenticationExpired()
    }
}