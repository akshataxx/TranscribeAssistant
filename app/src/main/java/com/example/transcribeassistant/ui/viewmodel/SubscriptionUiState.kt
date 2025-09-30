package com.example.transcribeassistant.ui.viewmodel

import com.example.transcribeassistant.domain.model.UsageInfo

sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()

    data class Success(
        val usageInfo: UsageInfo
    ) : SubscriptionUiState()

    data class Error(val message: String) : SubscriptionUiState()
}