package com.example.transcribeassistant.ui.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.billing.BillingManager
import com.example.transcribeassistant.domain.model.Subscription
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
    private val subscriptionRepository: SubscriptionRepository,
    private val billingManager: BillingManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()
    
    init {
        observePurchaseResults()
    }
    
    fun loadSubscriptionData() {
        viewModelScope.launch {
            try {
                _uiState.value = SubscriptionUiState.Loading
                
                val usageInfo = subscriptionRepository.getUsageInfo()
                val subscription = subscriptionRepository.getSubscriptionStatus()
                
                _uiState.value = SubscriptionUiState.Success(
                    usageInfo = usageInfo,
                    subscription = subscription
                )
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error("Failed to load subscription data: ${e.message}")
            }
        }
    }
    
    fun startPurchaseFlow(activity: ComponentActivity, productId: String) {
        viewModelScope.launch {
            try {
                val productDetails = billingManager.queryProductDetails()
                val targetProduct = productDetails.find { it.productId == productId }
                
                if (targetProduct != null) {
                    billingManager.launchBillingFlow(activity, targetProduct)
                } else {
                    _uiState.value = SubscriptionUiState.Error("Product not found")
                }
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error("Failed to start purchase: ${e.message}")
            }
        }
    }
    
    fun cancelSubscription() {
        viewModelScope.launch {
            try {
                subscriptionRepository.cancelSubscription()
                loadSubscriptionData() // Reload to show updated status
            } catch (e: Exception) {
                _uiState.value = SubscriptionUiState.Error("Failed to cancel subscription: ${e.message}")
            }
        }
    }
    
    private fun observePurchaseResults() {
        viewModelScope.launch {
            billingManager.purchaseResult.collect { result ->
                when (result) {
                    is BillingManager.PurchaseResult.Success -> {
                        handleSuccessfulPurchase(result.purchase)
                    }
                    is BillingManager.PurchaseResult.Error -> {
                        _uiState.value = SubscriptionUiState.Error("Purchase failed: ${result.message}")
                    }
                    is BillingManager.PurchaseResult.Cancelled -> {
                        // User cancelled, no action needed
                    }
                    is BillingManager.PurchaseResult.Pending -> {
                        // Purchase is pending, could show a pending state
                    }
                    null -> {
                        // No result yet
                    }
                }
            }
        }
    }
    
    private suspend fun handleSuccessfulPurchase(purchase: com.android.billingclient.api.Purchase) {
        try {
            // Verify purchase with backend
            val productId = purchase.products.firstOrNull() ?: return
            val subscription = subscriptionRepository.upgradeSubscription(
                purchaseToken = purchase.purchaseToken,
                productId = productId
            )
            
            // Clear the purchase result
            billingManager.clearPurchaseResult()
            
            // Reload subscription data
            loadSubscriptionData()
            
        } catch (e: Exception) {
            _uiState.value = SubscriptionUiState.Error("Failed to verify purchase: ${e.message}")
        }
    }
}

sealed class SubscriptionUiState {
    object Loading : SubscriptionUiState()
    
    data class Success(
        val usageInfo: UsageInfo,
        val subscription: Subscription?
    ) : SubscriptionUiState()
    
    data class Error(val message: String) : SubscriptionUiState()
}