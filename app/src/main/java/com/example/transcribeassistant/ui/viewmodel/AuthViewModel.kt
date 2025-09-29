package com.example.transcribeassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.data.auth.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authStateManager: AuthStateManager
) : ViewModel() {
    
    val authenticationExpired: SharedFlow<Unit> = authStateManager.authenticationExpired
}
