package com.example.transcribeassistant.ui.screen.subscription

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.domain.model.Subscription
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.ui.viewmodel.SubscriptionUiState
import com.example.transcribeassistant.ui.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadSubscriptionData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val currentState = uiState) {
                is SubscriptionUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is SubscriptionUiState.Success -> {
                    SubscriptionContent(
                        usageInfo = currentState.usageInfo,
                        subscription = currentState.subscription,
                        onUpgradeClick = { productId ->
                            viewModel.startPurchaseFlow(context as ComponentActivity, productId)
                        },
                        onCancelClick = { viewModel.cancelSubscription() }
                    )
                }
                
                is SubscriptionUiState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = { viewModel.loadSubscriptionData() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionContent(
    usageInfo: UsageInfo,
    subscription: Subscription?,
    onUpgradeClick: (String) -> Unit,
    onCancelClick: () -> Unit
) {
    // Usage Status Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (usageInfo.isPremium) Color(0xFF4CAF50) else Color(0xFFFFA726)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (usageInfo.isPremium) Icons.Default.Star else Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (usageInfo.isPremium) "Premium Active" else "Free Plan",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = usageInfo.usageMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    if (!usageInfo.isPremium) {
        // Premium Plans
        Text(
            text = "Upgrade to Premium",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Monthly Plan
        PremiumPlanCard(
            title = "Premium Monthly",
            price = "$9.99/month",
            features = listOf(
                "Unlimited transcriptions",
                "Priority processing",
                "Advanced categorization",
                "Export features"
            ),
            onSubscribeClick = { onUpgradeClick("premium_monthly") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Yearly Plan (with discount)
        PremiumPlanCard(
            title = "Premium Yearly",
            price = "$99.99/year",
            subtitle = "Save $20!",
            features = listOf(
                "Unlimited transcriptions",
                "Priority processing", 
                "Advanced categorization",
                "Export features",
                "2 months free"
            ),
            onSubscribeClick = { onUpgradeClick("premium_yearly") },
            isRecommended = true
        )
    } else {
        // Current Premium Subscription
        subscription?.let { sub ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = when (sub.subscriptionType) {
                            com.example.transcribeassistant.domain.model.SubscriptionType.PREMIUM_MONTHLY -> "Premium Monthly"
                            com.example.transcribeassistant.domain.model.SubscriptionType.PREMIUM_YEARLY -> "Premium Yearly"
                            else -> "Free"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    sub.subscriptionEndDate?.let { endDate ->
                        Text(
                            text = "Renews: ${endDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Subscription")
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumPlanCard(
    title: String,
    price: String,
    subtitle: String? = null,
    features: List<String>,
    onSubscribeClick: () -> Unit,
    isRecommended: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isRecommended) {
            CardDefaults.cardColors(containerColor = Color(0xFF5856D6))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (isRecommended) {
                Text(
                    text = "RECOMMENDED",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isRecommended) Color.White else MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = price,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isRecommended) Color.White else MaterialTheme.colorScheme.primary
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRecommended) Color.White else Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isRecommended) Color.White else Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRecommended) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubscribeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = if (isRecommended) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF5856D6)
                    )
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(
                    text = "Subscribe",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading subscription info",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}