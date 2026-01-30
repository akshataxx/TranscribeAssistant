package com.example.transcribeassistant.ui.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.example.transcribeassistant.BuildConfig
import com.example.transcribeassistant.billing.BillingClientWrapper
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
    private val billingClientWrapper: BillingClientWrapper
) : ViewModel() {

    companion object {
        private const val TAG = "SubscriptionViewModel"
    }

    private val _uiState = MutableStateFlow<SubscriptionUiState>(SubscriptionUiState.Loading)
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    init {
        setupBillingClient()
        observePurchases()
        observePurchaseErrors()
    }

    private fun setupBillingClient() {
        billingClientWrapper.startConnection()

        viewModelScope.launch {
            billingClientWrapper.connectionState.collect { connected ->
                if (connected) {
                    Log.d(TAG, "Billing client connected, querying product details")
                    queryProductDetails()
                }
            }
        }
    }

    private fun observePurchases() {
        viewModelScope.launch {
            billingClientWrapper.purchaseUpdates.collect { purchases ->
                Log.d(TAG, "Received purchase updates: ${purchases.size} purchases")
                handlePurchases(purchases)
            }
        }
    }

    private fun observePurchaseErrors() {
        viewModelScope.launch {
            billingClientWrapper.purchaseError.collect { error ->
                Log.w(TAG, "Purchase error: $error")
                // Don't show error state for cancellation
                if (!error.contains("cancelled", ignoreCase = true)) {
                    _uiState.value = SubscriptionUiState.Error(error)
                }
            }
        }
    }

    private suspend fun queryProductDetails() {
        val details = billingClientWrapper.queryProductDetails(BuildConfig.GOOGLE_PLAY_PRODUCT_ID_MONTHLY)
        _productDetails.value = details
        if (details != null) {
            Log.d(TAG, "Product details loaded: ${details.productId}, ${details.title}")
        } else {
            Log.w(TAG, "Failed to load product details for ${BuildConfig.GOOGLE_PLAY_PRODUCT_ID_MONTHLY}")
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        viewModelScope.launch {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    Log.d(TAG, "Processing purchased item: ${purchase.products}")

                    // Verify purchase with backend
                    try {
                        val productId = purchase.products.firstOrNull() ?: continue
                        val verified = subscriptionRepository.verifyPurchase(
                            productId = productId,
                            purchaseToken = purchase.purchaseToken,
                            orderId = purchase.orderId
                        )

                        if (verified) {
                            Log.d(TAG, "Purchase verified successfully")
                            // Reload usage info to reflect new subscription
                            loadUsageInfo()
                        } else {
                            Log.w(TAG, "Purchase verification failed")
                            _uiState.value = SubscriptionUiState.Error("Purchase verification failed. Please contact support.")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error verifying purchase", e)
                        _uiState.value = SubscriptionUiState.Error("Failed to verify purchase: ${e.message}")
                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Log.d(TAG, "Purchase is pending")
                    _uiState.value = SubscriptionUiState.Error("Payment is pending. Please complete the payment.")
                }
            }
        }
    }

    fun loadUsageInfo() {
        viewModelScope.launch {
            try {
                _uiState.value = SubscriptionUiState.Loading
                val usageInfo = subscriptionRepository.getUsageInfo()
                _uiState.value = SubscriptionUiState.Success(usageInfo = usageInfo)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load usage info", e)
                _uiState.value = SubscriptionUiState.Error("Failed to load usage info: ${e.message}")
            }
        }
    }

    /**
     * Start the premium upgrade flow using Google Play Billing.
     *
     * @param activity The activity to launch the billing flow from
     */
    fun startPremiumUpgrade(activity: Activity) {
        val details = _productDetails.value
        if (details != null) {
            Log.d(TAG, "Launching billing flow for: ${details.productId}")
            val result = billingClientWrapper.launchBillingFlow(activity, details)
            Log.d(TAG, "Billing flow result: ${result.responseCode}")
        } else {
            Log.e(TAG, "Product details not available")
            _uiState.value = SubscriptionUiState.Error("Subscription not available. Please try again later.")
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            try {
                subscriptionRepository.cancelSubscription()
                // Reload usage info to reflect cancellation
                loadUsageInfo()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel subscription", e)
                _uiState.value = SubscriptionUiState.Error("Failed to cancel subscription: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared, ending billing connection")
        billingClientWrapper.endConnection()
    }
}
