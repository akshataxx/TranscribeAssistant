package com.example.transcribeassistant.ui.viewmodel

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val accessTokenPreview: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}