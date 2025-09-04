package com.example.transcribeassistant.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    private val context: Context
) : PurchasesUpdatedListener, BillingClientStateListener {
    
    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    
    private val _billingConnectionState = MutableStateFlow(BillingConnectionState.DISCONNECTED)
    val billingConnectionState: StateFlow<BillingConnectionState> = _billingConnectionState.asStateFlow()
    
    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult.asStateFlow()
    
    // Product IDs - these should match your Google Play Console configuration
    companion object {
        const val PREMIUM_MONTHLY_PRODUCT_ID = "premium_monthly"
        const val PREMIUM_YEARLY_PRODUCT_ID = "premium_yearly"
    }
    
    init {
        startConnection()
    }
    
    private fun startConnection() {
        billingClient.startConnection(this)
    }
    
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _billingConnectionState.value = BillingConnectionState.CONNECTED
        } else {
            _billingConnectionState.value = BillingConnectionState.ERROR
        }
    }
    
    override fun onBillingServiceDisconnected() {
        _billingConnectionState.value = BillingConnectionState.DISCONNECTED
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _purchaseResult.value = PurchaseResult.Cancelled
        } else {
            _purchaseResult.value = PurchaseResult.Error("Purchase failed: ${billingResult.debugMessage}")
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }
            _purchaseResult.value = PurchaseResult.Success(purchase)
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            _purchaseResult.value = PurchaseResult.Pending
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            // Handle acknowledgment result if needed
        }
    }
    
    suspend fun queryProductDetails(): List<ProductDetails> {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_MONTHLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_YEARLY_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        val result = billingClient.queryProductDetails(params)
        return result.productDetailsList ?: emptyList()
    }
    
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }
    
    fun clearPurchaseResult() {
        _purchaseResult.value = null
    }
    
    enum class BillingConnectionState {
        DISCONNECTED,
        CONNECTED,
        ERROR
    }
    
    sealed class PurchaseResult {
        object Pending : PurchaseResult()
        object Cancelled : PurchaseResult()
        data class Success(val purchase: Purchase) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }
}