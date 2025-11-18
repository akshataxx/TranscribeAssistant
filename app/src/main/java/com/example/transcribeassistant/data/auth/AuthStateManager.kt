package com.example.transcribeassistant.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor() {
    
    private val _authenticationExpired = MutableSharedFlow<Unit>(replay = 0)
    val authenticationExpired: SharedFlow<Unit> = _authenticationExpired.asSharedFlow()
    
    fun signalAuthenticationExpired() {
        _authenticationExpired.tryEmit(Unit)
    }
}
