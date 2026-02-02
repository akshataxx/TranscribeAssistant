package com.example.transcribeassistant.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Wrapper class for managing Google Play Billing operations.
 * Handles connection, product queries, purchases, and purchase updates.
 */
@Singleton
class BillingClientWrapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "BillingClientWrapper"
    }

    private var billingClient: BillingClient? = null

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private val _purchaseUpdates = MutableSharedFlow<List<Purchase>>(extraBufferCapacity = 1)
    val purchaseUpdates: SharedFlow<List<Purchase>> = _purchaseUpdates.asSharedFlow()

    private val _purchaseError = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val purchaseError: SharedFlow<String> = _purchaseError.asSharedFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "Purchase successful: ${purchases?.size} items")
                purchases?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        _purchaseUpdates.emit(it)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled the purchase")
                CoroutineScope(Dispatchers.Main).launch {
                    _purchaseError.emit("Purchase was cancelled")
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item already owned")
                // Query existing purchases to handle this case
                CoroutineScope(Dispatchers.Main).launch {
                    val existingPurchases = queryPurchases()
                    if (existingPurchases.isNotEmpty()) {
                        _purchaseUpdates.emit(existingPurchases)
                    }
                }
            }
            else -> {
                Log.e(TAG, "Purchase failed: ${billingResult.debugMessage} (code: ${billingResult.responseCode})")
                CoroutineScope(Dispatchers.Main).launch {
                    _purchaseError.emit("Purchase failed: ${billingResult.debugMessage}")
                }
            }
        }
    }

    /**
     * Start the billing client connection.
     * Must be called before any other billing operations.
     */
    fun startConnection() {
        Log.d(TAG, "Starting billing client connection")

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected successfully")
                    _connectionState.value = true
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                    _connectionState.value = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                _connectionState.value = false
                // Implement retry logic if needed
            }
        })
    }

    /**
     * Query product details for a given product ID.
     *
     * @param productId The product ID to query (e.g., "premium_monthly")
     * @return ProductDetails if found, null otherwise
     */
    suspend fun queryProductDetails(productId: String): ProductDetails? {
        val client = billingClient ?: run {
            Log.e(TAG, "Billing client not initialized")
            return null
        }

        if (!_connectionState.value) {
            Log.e(TAG, "Billing client not connected")
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val details = productDetailsList.firstOrNull()
                    Log.d(TAG, "Product details query successful: ${details?.productId}")
                    continuation.resume(details)
                } else {
                    Log.e(TAG, "Failed to query product details: ${billingResult.debugMessage}")
                    continuation.resume(null)
                }
            }
        }
    }

    /**
     * Launch the billing flow to purchase a subscription.
     *
     * @param activity The activity to launch the billing flow from
     * @param productDetails The product details for the subscription
     * @return BillingResult indicating success or failure
     */
    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails): BillingResult {
        val client = billingClient ?: return BillingResult.newBuilder()
            .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
            .setDebugMessage("Billing client not initialized")
            .build()

        // Get the first offer token (base plan)
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
        if (offerToken == null) {
            Log.e(TAG, "No offer token found for product: ${productDetails.productId}")
            return BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.ERROR)
                .setDebugMessage("No subscription offer found")
                .build()
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        Log.d(TAG, "Launching billing flow for: ${productDetails.productId}")
        return client.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Query existing subscription purchases.
     *
     * @return List of active purchases
     */
    suspend fun queryPurchases(): List<Purchase> {
        val client = billingClient ?: run {
            Log.e(TAG, "Billing client not initialized")
            return emptyList()
        }

        if (!_connectionState.value) {
            Log.e(TAG, "Billing client not connected")
            return emptyList()
        }

        return suspendCancellableCoroutine { continuation ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            client.queryPurchasesAsync(params) { billingResult, purchasesList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Query purchases successful: ${purchasesList.size} items")
                    continuation.resume(purchasesList)
                } else {
                    Log.e(TAG, "Failed to query purchases: ${billingResult.debugMessage}")
                    continuation.resume(emptyList())
                }
            }
        }
    }

    /**
     * End the billing client connection.
     * Should be called when the billing client is no longer needed.
     */
    fun endConnection() {
        Log.d(TAG, "Ending billing client connection")
        billingClient?.endConnection()
        _connectionState.value = false
    }
}
