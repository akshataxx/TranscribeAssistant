package com.example.transcribeassistant.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    @ApplicationContext private val context: Context
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

    fun startPremiumUpgrade() {
        viewModelScope.launch {
            try {
                val checkoutUrl = subscriptionRepository.createStripeCheckout("price_1SDccWBIj51ZSIefUfPLTqxf")
                openUrlInBrowser(checkoutUrl)
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error("Failed to start upgrade: ${e.message}")
            }
        }
    }

    private fun openUrlInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}