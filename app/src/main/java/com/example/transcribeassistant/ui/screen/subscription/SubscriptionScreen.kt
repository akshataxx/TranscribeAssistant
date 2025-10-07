package com.example.transcribeassistant.ui.screen.subscription

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.SubscriptionViewModel
import com.example.transcribeassistant.ui.viewmodel.SubscriptionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState: SubscriptionUiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUsageInfo()
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
            when (val state = uiState) {
                is SubscriptionUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is SubscriptionUiState.Success -> {
                    SubscriptionContent(
                        usageInfo = state.usageInfo,
                        onUpgradeClick = {
                            viewModel.startPremiumUpgrade()
                        }
                    )
                }

                is SubscriptionUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadUsageInfo() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionContent(
    usageInfo: com.example.transcribeassistant.domain.model.UsageInfo,
    onUpgradeClick: () -> Unit
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
        
        // Premium Plan Card
        PremiumPlanCard(
            title = "Premium Plan",
            price = "$3.99/month",
            features = listOf(
                "Unlimited transcriptions",
                "Priority processing",
                "Advanced categorization",
                "Export features"
            ),
            onSubscribeClick = onUpgradeClick
        )
    }
}

@Composable
private fun PremiumPlanCard(
    title: String,
    price: String,
    features: List<String>,
    onSubscribeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5856D6))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "RECOMMENDED",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = price,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubscribeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF5856D6)
                )
            ) {
                Text(
                    text = "Subscribe - $3.99/month",
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