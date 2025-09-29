package com.example.transcribeassistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    fun loadUsageInfo() {
        viewModelScope.launch {
            try {
                _uiState.value = SubscriptionUiState.Loading
                val usageInfo = subscriptionRepository.getUsageInfo()
                _uiState.value = SubscriptionUiState.Success(usageInfo = usageInfo)
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error("Failed to load usage info: ${e.message}")
            }
        }
    }
}

sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    
    data class Success(
        val usageInfo: UsageInfo
    ) : SubscriptionUiState()
    
    data class Error(val message: String) : SubscriptionUiState()
}